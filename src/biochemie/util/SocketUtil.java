package biochemie.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/** A shorthand way to create BufferedReaders and
* PrintWriters associated with a Socket.
*/
public class SocketUtil {
/** Make a BufferedReader to get incoming data. */
public static BufferedReader getReader(Socket s)
throws IOException {
return(new BufferedReader(
new InputStreamReader(s.getInputStream())));
}
/** Make a PrintWriter to send outgoing data.
* This PrintWriter will automatically flush stream
* when println is called.
*/
public static PrintWriter getWriter(Socket s)
throws IOException {
// 2nd argument of true means autoflush
return(new PrintWriter(s.getOutputStream(), true));
}
}