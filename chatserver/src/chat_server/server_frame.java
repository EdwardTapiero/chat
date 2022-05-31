package chat_server;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class server_frame extends javax.swing.JFrame {
    ArrayList clientOutputStreams;
    ArrayList<String> users;
    private javax.swing.JTextArea ta_chat;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ta_chat.append("Error inesperado... \n");
            }

        }

        @Override
        public void run() {
            String message, connect = "Conectar", disconnect = "Desconectar", chat = "Chat";
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    ta_chat.append("\n<<Recibido de " + message + "\n");
                    data = message.split(":");

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":fue desconectado." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                    } else {
                        ta_chat.append("No se cumplen las condiciones. \n");
                    }
                }
            } catch (Exception ex) {
                ta_chat.append("Se ha perdido la conexión. \n");
                clientOutputStreams.remove(client);
            }
        }
    }

    public server_frame() {
        initComponents();
    }

    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        javax.swing.JButton b_start = new javax.swing.JButton();
        javax.swing.JButton b_users = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor");
        setBackground(new java.awt.Color(0, 0, 203));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("server"); // NOI18N
        setResizable(false);

        ta_chat.setEditable(false);
        ta_chat.setColumns(20);
        ta_chat.setFont(new java.awt.Font("Verdana", Font.PLAIN, 13)); // NOI18N
        ta_chat.setForeground(new java.awt.Color(0, 0, 204));
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_start.setText("Iniciar");
        b_start.addActionListener(this::b_startActionPerformed);

        b_users.setText("Usuarios");
        b_users.setActionCommand("Users");
        b_users.addActionListener(this::b_usersActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(b_start, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(29, 29, 29)
                                                .addComponent(b_users, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(b_start)
                                        .addComponent(b_users))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }

    private void b_startActionPerformed(java.awt.event.ActionEvent evt) {
        Thread starter = new Thread(new ServerStart());
        starter.start();

        ta_chat.append("Servidor Iniciado...\n");
    }

    private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {
        ta_chat.append("\n Usuarios en línea : \n");
        for (String current_user : users) {
            ta_chat.append(current_user);
            ta_chat.append("\n");
        }

    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new server_frame().setVisible(true));
    }

    public class ServerStart implements Runnable {
        @Override
        public void run() {
            clientOutputStreams = new ArrayList<>();
            users = new ArrayList<>();

            try {
                ServerSocket serverSock = new ServerSocket(2222);

                while (true) {
                    Socket clientSock = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();
                    ta_chat.append("Se obtuvo conexión. \n");
                }
            } catch (Exception ex) {
                ta_chat.append("Error al establecer conexión. \n");
            }
        }
    }

    public void userAdd(String data) {
        String message, add = ": :Conectar", done = "Servidor: :Realizado";
        users.add(data);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void userRemove(String data) {
        String message, add = ": :Conectar", done = "Servidor: :Realizado";
        users.remove(data);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {

        for (Object clientOutputStream : clientOutputStreams) {
            PrintWriter writer = (PrintWriter) clientOutputStream;
            writer.println(message);
            ta_chat.append(">>Enviando: " + message + "\n");
            writer.flush();
            ta_chat.setCaretPosition(ta_chat.getDocument().getLength());

        }
    }
}
