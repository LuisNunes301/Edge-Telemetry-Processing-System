import requests
import time
import random
import json
from datetime import datetime, timezone

# Configuração do endpoint do Gateway (que você criará no Spring Boot)
API_URL = "http://localhost:8080/api/telemetry"

# Coordenadas iniciais
current_lat = -12.6975
current_lng = -38.3239
battery = 100

def generate_payload():
    global current_lat, current_lng, battery
    
    current_lat += random.uniform(-0.0005, 0.0005)
    current_lng += random.uniform(-0.0005, 0.0005)
    
    if battery > 0:
        battery -= random.uniform(0.1, 0.5)

    return {
        "deviceId": "TRK-CAM-092",
        "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "location": {
            "latitude": round(current_lat, 6),
            "longitude": round(current_lng, 6)
        },
        "metrics": {
            "temperature": round(random.uniform(-5.0, 5.0), 2),
            "humidity": round(random.uniform(40.0, 60.0), 1),
            "batteryLevel": round(battery, 1)
        },
        "metadata": {
            "firmwareVersion": "1.2.4",
            "networkType": "4G"
        }
    }

# Gera o primeiro payload e salva no arquivo expected.json
sample_payload = generate_payload()
with open("expected.json", "w", encoding="utf-8") as json_file:
    json.dump(sample_payload, json_file, indent=2)

print("Arquivo 'expected.json' gerado e salvo no diretório atual.")
print("Iniciando envio para a API... Pressione Ctrl+C para parar.\n")

while True:
    payload = generate_payload()
    
    try:
        response = requests.post(API_URL, json=payload, timeout=2)
        print(f"[{payload['timestamp']}] Enviado: Temp {payload['metrics']['temperature']}°C | Bateria {payload['metrics']['batteryLevel']}% | Status API: {response.status_code}")
    except requests.exceptions.ConnectionError:
        print(f"[{payload['timestamp']}] Falha na conexão. O Device Gateway (localhost:8080) está rodando?")
    
    time.sleep(3)