
package main

import (
	"net"
	"./klog"
	"fmt"
)

var SockServ = klog.Module("Socket Server")

// Don't need to close the conneciton when done.
// errors returned will be logged accordingly.
type ConnHandler func(net.Conn) error

func handleConn(conn net.Conn, h ConnHandler) {
	defer conn.Close()
	
	fmt.Println("Handle conn starting!")
	defer fmt.Println("Handle conn ended!")
	if err := h(conn); err != nil {
		defer fmt.Println("Found err when running: ", err)
		klog.Warning(SockServ, "serving connection: ", err)
	}
}

func runServer(addr string, h ConnHandler) {
	klog.Info(SockServ, "Opening on port ", addr)
	l, err := net.Listen("tcp", addr)
	if err != nil {
		panic(err)
	}
	for {
		conn, err := l.Accept()
		if err != nil {
			klog.Warning(SockServ, "accepting connection: ", err)
			continue
		}
		klog.Info(SockServ, "Got conn")
		go handleConn(conn, h)
	}
}