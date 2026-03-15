using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.IO;

namespace bai1
{
    public class DateClient
    {
        public static void Run()
        {
            const string serverIp = "localhost";
            const int port = 5001;
            try
            {
                using (TcpClient client = new TcpClient())
                {
                    client.Connect(serverIp, port);
                    using (NetworkStream ns = client.GetStream())
                    using (StreamReader sr = new StreamReader(ns, Encoding.UTF8))
                    {
                        string? time = sr.ReadLine();
                        Console.WriteLine("Ngày tháng năm từ Server: " + (time ?? "(no data)"));
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
        }
    }
}