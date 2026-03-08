import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const WEBSOCKET_URL = 'http://localhost:8081/ws';

class WebSocketService {
    constructor() {
        this.client = new Client({
            // Using factory to allow SockJS fallback
            webSocketFactory: () => new SockJS(WEBSOCKET_URL),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });
    }

    connect(onFlightsUpdate, onAlertsUpdate) {
        this.client.onConnect = (frame) => {
            console.log('Connected to WebSocket server:', frame);

            this.client.subscribe('/topic/flights', (message) => {
                if (message.body) {
                    onFlightsUpdate(JSON.parse(message.body));
                }
            });

            this.client.subscribe('/topic/alerts', (message) => {
                if (message.body) {
                    onAlertsUpdate(JSON.parse(message.body));
                }
            });
        };

        this.client.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        this.client.activate();
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
        }
    }
}

const webSocketService = new WebSocketService();
export default webSocketService;
