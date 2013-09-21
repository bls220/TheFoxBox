
package main

import (
	"net"
)

type ConnHandler func(net.Conn) error

func handleConn(conn net.Conn, h ConnHandler) {
	defer conn.Close()
	
	if err := h(conn); err != nil {
		Warning(SockServ, "serving connection: ", err)
	}
}

func runServer(addr string, h ConnHandler) {
	l, err := net.Listen("tcp", addr)
	if err != nil {
		panic(err)
	}
	for {
		conn, err := l.Accept()
		if err != nil {
			Warning(SockServ, "accepting connection: ", err)
			continue
		}
		go handleConn(conn, h)
	}
}