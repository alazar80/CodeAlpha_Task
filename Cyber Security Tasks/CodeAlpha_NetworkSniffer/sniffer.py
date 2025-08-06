from scapy.all import sniff, IP, TCP, UDP

def packet_callback(packet):
    if IP in packet:
        ip_src = packet[IP].src
        ip_dst = packet[IP].dst
        proto = packet[IP].proto
        summary = f"IP Packet: {ip_src} -> {ip_dst} | Protocol: {proto}"

        if TCP in packet:
            summary += f" | TCP Port: {packet[TCP].sport} -> {packet[TCP].dport}"
        elif UDP in packet:
            summary += f" | UDP Port: {packet[UDP].sport} -> {packet[UDP].dport}"

        print(summary)

        with open("captured_packets.txt", "a") as f:
            f.write(summary + "\n")

print("🔎 Starting packet capture... Press CTRL+C to stop.\n")
sniff(prn=packet_callback, count=50)  # Capture 50 packets (change as needed)
