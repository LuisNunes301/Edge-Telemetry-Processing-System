import requests
import time
import random
from datetime import datetime, timezone
from concurrent.futures import ThreadPoolExecutor
import os

API_URL = os.getenv("API_URL", "http://gateway:8080/api/telemetry")
NUM_DEVICES = 100  # 100 dispositivos virtuais rodando simultaneamente no mesmo container
REQUEST_INTERVAL = 2.0  # Intervalo de envio por dispositivo

def simulate_device(device_id):
    # Estado inicial único e dinâmico para cada dispositivo simulado
    current_lat = -12.6975 + random.uniform(-0.05, 0.05)
    current_lng = -38.3239 + random.uniform(-0.05, 0.05)
    battery = round(random.uniform(50.0, 100.0), 1)
    
    print(f" [Dispositivo TRK-CAM-{device_id:03d}] Iniciado na thread.")
    
    while True:
        current_lat += random.uniform(-0.0002, 0.0002)
        current_lng += random.uniform(-0.0002, 0.0002)
        
        if battery > 5.0:
            battery -= round(random.uniform(0.01, 0.05), 2)

        payload = {
            "deviceId": f"TRK-CAM-{device_id:03d}",
            "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
            "location": {
                "latitude": round(current_lat, 6),
                "longitude": round(current_lng, 6)
            },
            "metrics": {
                "temperature": round(random.uniform(-5.0, 35.0), 2),
                "humidity": round(random.uniform(40.0, 90.0), 1),
                "batteryLevel": battery
            },
            "metadata": {
                "firmwareVersion": "1.2.4",
                "networkType": "4G"
            }
        }
        
        try:
            response = requests.post(API_URL, json=payload, timeout=2)
            print(f"[{payload['deviceId']}] Status: {response.status_code} | Temp: {payload['metrics']['temperature']}°C")
        except requests.exceptions.RequestException:
            print(f"[{payload['deviceId']}] Falha na conexão com o Gateway.")
            
        time.sleep(REQUEST_INTERVAL)

def main():
    print(f" Iniciando Enxame IoT: Simulando {NUM_DEVICES} dispositivos concorrentes em um único container...")
    with ThreadPoolExecutor(max_workers=NUM_DEVICES) as executor:
        executor.map(simulate_device, range(1, NUM_DEVICES + 1))

if __name__ == "__main__":
    main()