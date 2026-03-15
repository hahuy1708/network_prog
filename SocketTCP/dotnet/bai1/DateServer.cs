using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace bai1
{
    public class DateServer
    {
        public static void Run()
        {
            const int port = 5001;
            TcpListener? listener = null;
            try
            {
                listener = new TcpListener(IPAddress.Any, port);
                listener.Start();
                Console.WriteLine($"Server listening on port {port}...");

                while (true)
                {
                    using (TcpClient client = listener.AcceptTcpClient())
                    using (NetworkStream ns = client.GetStream())
                    {
                        string dateStr = DateTime.Now.ToString();
                        byte[] data = Encoding.UTF8.GetBytes(dateStr + Environment.NewLine);
                        ns.Write(data, 0, data.Length);
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
            finally
            {
                listener?.Stop();
            }
        }
    }
}