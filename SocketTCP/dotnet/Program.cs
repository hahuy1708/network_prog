using bai1;

if (args.Length < 1)
{
	Console.WriteLine("Usage: dotnet run -- <problem> <role>\nExample: dotnet run -- bai1 server\n         dotnet run -- bai1 client");
	return;
}

string problem = args[0].ToLower();
string role = args.Length > 1 ? args[1].ToLower() : "server";

if (problem == "bai1")
{
	if (role == "server") DateServer.Run();
	else if (role == "client") DateClient.Run();
	else Console.WriteLine("Unknown role. Use 'server' or 'client'.");
}
else
{
	Console.WriteLine($"Unknown problem: {problem}. Implement other problems (bai2, bai3) in dotnet project first.");
}
