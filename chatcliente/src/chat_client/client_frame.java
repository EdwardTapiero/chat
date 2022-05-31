package chat_client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class client_frame extends javax.swing.JFrame {
    String username;
    String address = "localhost";
    ArrayList<String> users = new ArrayList<>();
    int port = 2222;
    Boolean isConnected = false;
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;
    private javax.swing.JTextArea ta_chat;
    private javax.swing.JTextField tf_address;
    private javax.swing.JTextField tf_chat;
    private javax.swing.JTextField tf_username;

    public void ListenThread() {
        Thread IncomingReader = new Thread(new IncomingReader());
        IncomingReader.start();
    }

    public void userAdd(String data) {
        users.add(data);
    }

    public void userRemove(String data) {
        ta_chat.append(data + " está fuera de línea.\n");
    }

    public void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
    }

    public void sendDisconnect() {
        String bye = (username + ":fue desconectado.:Desconectar");
        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            ta_chat.append("No se ha podido enviar el mensaje de desconexión.\n");
        }
    }

    public void Disconnect() {
        try {
            ta_chat.append("Desconectado.\n");
            sock.close();
        } catch (Exception ex) {
            ta_chat.append("Falla al desconectar. \n");
        }
        isConnected = false;
        tf_username.setEditable(true);

    }

    public client_frame() {
        initComponents();
    }

    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            String[] data;
            String stream, done = "Realizado", connect = "Conectar", disconnect = "Desconectar", chat = "Chat";

            try {
                while ((stream = reader.readLine()) != null) {
                    data = stream.split(":");

                    if (data[2].equals(chat)) {

                        if (data[1].equals("fue conectado.") || data[1].equals("Desconectado."))
                            ta_chat.append(data[0] + ": " + data[1] + "\n");
                        else
                            ta_chat.append(data[0] + ":     " + data[1] + "\n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                    } else if (data[2].equals(connect)) {
                        ta_chat.removeAll();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);
                    } else if (data[2].equals(done)) {
                        writeUsers();
                        users.clear();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void initComponents() {

        javax.swing.JLabel lb_address = new javax.swing.JLabel();
        tf_address = new javax.swing.JTextField();
        javax.swing.JLabel lb_username = new javax.swing.JLabel();
        tf_username = new javax.swing.JTextField();
        javax.swing.JButton b_connect = new javax.swing.JButton();
        javax.swing.JButton b_disconnect = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        tf_chat = new javax.swing.JTextField();
        javax.swing.JButton b_send = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cliente");
        setName("client");
        setResizable(false);

        lb_address.setText("Dirección IP : ");

        tf_address.setText("localhost");
        tf_address.addActionListener(this::tf_addressActionPerformed);

        lb_username.setText("Nombre de usuario :");
        tf_username.addActionListener(this::tf_usernameActionPerformed);

        b_connect.setText("Conectar");
        b_connect.addActionListener(this::b_connectActionPerformed);

        b_disconnect.setText("Desconectar");
        b_disconnect.addActionListener(this::b_disconnectActionPerformed);

        ta_chat.setEditable(false);
        ta_chat.setColumns(20);
        ta_chat.setFont(new java.awt.Font("Verdana", Font.PLAIN, 13)); // NOI18N
        ta_chat.setRows(5);
        ta_chat.setAlignmentX(1.0F);
        ta_chat.setAlignmentY(1.0F);
        jScrollPane1.setViewportView(ta_chat);

        b_send.setText("Enviar mensaje");
        b_send.addActionListener(evt -> b_sendActionPerformed());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(lb_username, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                                        .addComponent(lb_address, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(tf_address, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                                        .addComponent(tf_username))
                                                .addGap(28, 28, 28)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(b_connect, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(b_disconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(28, 28, 28)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(tf_chat, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                                .addGap(15, 15, 15))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lb_address)
                                        .addComponent(tf_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(b_connect))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(tf_username)
                                                .addComponent(b_disconnect))
                                        .addComponent(lb_username))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tf_chat)
                                        .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }

    private void tf_addressActionPerformed(java.awt.event.ActionEvent evt) {}

    private void tf_usernameActionPerformed(java.awt.event.ActionEvent evt) {}

    private void b_connectActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isConnected) {
            username = tf_username.getText();
            tf_username.setEditable(false);
            address = tf_address.getText();
            tf_address.setEditable(false);
            port = 2222;


            try {
                sock = new Socket(address, port);

                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);

                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":fue conectado.:Conectar");
                writer.flush();

                isConnected = true;
            } catch (Exception ex) {
                ta_chat.append("No se pudo conectar! Intente de nuevo. \n");
                tf_username.setEditable(true);
            }

            ListenThread();

        } else {
            ta_chat.append("Ya estas conectado. \n");
        }
    }

    private void b_disconnectActionPerformed(java.awt.event.ActionEvent evt) {
        sendDisconnect();
        Disconnect();
    }

    private void b_sendActionPerformed() {

        String nothing = "";
        if (!(tf_chat.getText()).equals(nothing)) {
            try {

                if(tf_chat.getText().equals("adios")){
                    sendDisconnect();
                    Disconnect();

                } else {
                    writer.println(username + ":" + tf_chat.getText() + ":" + "Chat");
                    writer.flush();
                }

            } catch (Exception ex) {
                ta_chat.append("Mensaje no fue enviado. \n");
            }
        }
        tf_chat.setText("");
        tf_chat.requestFocus();

        tf_chat.setText("");
        tf_chat.requestFocus();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new client_frame().setVisible(true));
    }
}
