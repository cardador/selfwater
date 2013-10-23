# -*- coding: utf-8 -*-

# -*- coding: utf-8 -*-

import serial
import time
import socket
import threading
import SocketServer

class Simple():
    """\
    Single port serial<->TCP/IP forarder 
    """

    def __init__(self, device=None, on_close=None):
        
        self.alive = False
        self.on_close = on_close
        self.serial = serial.Serial()
        if device is None:
            self.serial.port = '/dev/ttyUSB0'
        else:
            self.serial.port = device
        
        self.serial.baudrate = 115200
        self.serial.parity = serial.PARITY_NONE
        self.serial.bytesize = serial.EIGHTBITS
        self.serial.stopbits = serial.STOPBITS_ONE
        self.serial.timeout = 1.5 #1.5 to give the hardware handshake time to happen
        self.serial.xonxoff = False
        self.serial.rtscts = False
        self.serial.dsrdtr = False

    def dump(self, dump=None):
        if dump:
            try:
                self.serial.open()
                self.serial.setRTS(False)
            except Exception, msg:
                print 'Opss! Failed to open serial'
            
            self.serial.readline()
            if 'hum' in dump:
                self.serial.write('i\r\n')
                self.output = self.serial.readline() + '\n'
            if 'dry' in dump:
                self.serial.write('h\r\n')
                self.output = self.serial.readline() + '\n'
            if 'sen0' in dump:
                self.serial.write('j\r\n')
                self.output = self.serial.readline() + '\n'
            if 'sen1' in dump:
                self.serial.write('k\r\n')
                self.output = self.serial.readline() + '\n'
            if 'elap' in dump:
                self.serial.write('l\r\n')
                self.output = self.serial.readline() + '\n'
            if 'echo' in dump:
                self.output = dump + '\n'

            self.serial.close()
            
class ThreadedTCPRequestHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        self.simple = Simple(device='/dev/ttyUSB0')
        while 1:
            data = self.request.recv(1024)
            
            if not data:
                break
            self.simple.dump(dump=data)
            self.request.send(self.simple.output)

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    pass
        
class Server():
    
    def __init__(self, port=None):
        self.HOST = ''
        if port is None:
            self.PORT = 4444              # Arbitrary non-privileged port
        else:
            self.PORT = port
            
        server = ThreadedTCPServer((self.HOST, self.PORT), ThreadedTCPRequestHandler)
    
        # Start a thread with the server -- that thread will then start one
        # more thread for each request
        server_thread = threading.Thread(target=server.serve_forever)
        # Exit the server thread when the main thread terminates
        #server_thread.daemon = True
        server_thread.start()

        
if __name__ == '__main__':
    s = Server(port=4444)
