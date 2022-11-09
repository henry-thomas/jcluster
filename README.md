# jcluster

A peer to peer messaging system for Java applications which is simple to use, for use cases where transactions aren't required and message persistence not a priority.

Uses a combination of Hazelcast for member discovery, plain sockets with java serialization for connections and messaging, dynamic proxies, and jndi lookup for remote method invocation.
