/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcluster.sockets;

import org.jcluster.messages.JcMessage;
import org.jcluster.utils.MessageUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcluster.messages.JcMsgResponse;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 *
 * @author henry
 */
//@Singleton
public class JcAppInstance implements Runnable, IConnection {

    private static final Logger LOG = Logger.getLogger(JcAppInstance.class.getName());
    private int retriesLeft = 3; //A connection will try send 3 times before giving up. Then needs to be called explicitly to try again 3 times, until it succeeds.
    private final String serverEndpoint;
    private final int requestTimeout = 2000;

//    JcContext context = JcContext.getInstance();
    ZContext context = JcContext.getInstance().getContext();
    ZMQ.Socket socket;
    ZMQ.Poller poller;

    public JcAppInstance(String bindAddress) {
        serverEndpoint = bindAddress;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("JcClient-Thread");

        socket = context.createSocket(SocketType.REQ);
        socket.setReceiveTimeOut(requestTimeout);
        socket.setSendTimeOut(requestTimeout);
        socket.connect(serverEndpoint);

        poller = context.createPoller(1);
        poller.register(socket, ZMQ.Poller.POLLIN);
//        }
    }

    @Override
    public JcMessage send(JcMessage msg) {
        boolean expect_reply = false;

        byte[] reply = null;
        JcMessage response = null;

        try {
            boolean send = socket.send(MessageUtils.srlz(msg));

            if (!send) {
                return null;
            }

            expect_reply = true;
            while (expect_reply) {
                //poll for receive event
                int receive = poller.poll(requestTimeout);

                if (receive == -1) {
                    break;
                }

                if (poller.pollin(0)) {
                    reply = socket.recv();
                    if (reply == null) {
                        break;
                    }

                    //Match sent message ID to received message ID here
                    response = MessageUtils.dsrlz(reply);
                    JcMsgResponse msgRest = response.getResponse();
                    if (msgRest.getRequestId() == msg.getRequestId()) {
                        LOG.log(Level.INFO, "Valid reply received: {0}", response.getRequestId());
                        retriesLeft = 3;
                        expect_reply = false;
                    }

                } else if (--retriesLeft == 0) {
                    LOG.severe("server seems to be offline, abandoning");
                    retriesLeft = 3;
                    expect_reply = false;
                    break;
                } else {
                    reconnect(msg);
                }
            }

        } catch (Exception e) {
//            response = new JcMsgResponse();
//            response.setData(e.getMessage());
            LOG.warning(e.getMessage());
        }

        if (response != null) {
            return response;
        } else {
            LOG.log(Level.INFO, "Receive Timeout");
//            response = new JcMessage();
//            response.setData("Receive timeout");
//            msg.setResponse(response);
        }

        return response;
    }

    private JcMessage reconnect(JcMessage msg) {
        LOG.log(Level.SEVERE, "Attempting reconnect... Retries left: {0}", retriesLeft);

        poller.unregister(socket);
        context.destroySocket(socket);

        socket = context.createSocket(SocketType.REQ);
        poller.register(socket, ZMQ.Poller.POLLIN);

        boolean connect = socket.connect(serverEndpoint);
        if (retriesLeft == 3 && connect) {
            JcMessage retryMsg = send(msg);
            return retryMsg;
        } else {
            socket.send(MessageUtils.srlz(msg));
        }
        return null;
    }

    @Override
    public void destroy() {
        LOG.log(Level.INFO, "Closing socket");

        context.destroySocket(socket);
    }

    public String printDescription() {
        return "Connected to Server at: " + serverEndpoint;
    }

}
