package com.gutierrez.knightsduty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity
{
    private ViewGroup mContentView;
    private MyGLSurfaceView mGLSurfaceView;
    private DrawerLayout mShopDrawer;
    private RecyclerView mShopRecyclerView;
    private ShopAdapter mAdapter;

    private TextView mGoldDisplay;
    private TextView mHealthDisplay;

    private ServerSocket ss;

    private static AsyncTask<Void, Void, Void> async;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get content view and hide title and nav bar
        mContentView = findViewById(R.id.activity_main);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mGLSurfaceView = findViewById(R.id.openGLView);
        mGLSurfaceView.setParent(this);
        /*mGLSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);*/

        mShopRecyclerView = findViewById(R.id.shop_list);
        mShopRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mGoldDisplay = findViewById(R.id.goldTextView);
        mGoldDisplay.setText("0");

        mHealthDisplay = findViewById(R.id.healthTextView);
        mHealthDisplay.setText("500");

        mShopDrawer = findViewById(R.id.drawer_layout);
        mShopDrawer.setScrimColor(Color.TRANSPARENT); //prevents darkening of screen when drawer is opened
        mShopDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //prevents user opening the shop menu by sliding
        mShopDrawer.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(@NonNull View view, float v)
            {
            }

            @Override
            public void onDrawerOpened(@NonNull View view)
            {
                mShopDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //allows user to close shop menu by sliding
            }

            @Override
            public void onDrawerClosed(@NonNull View view)
            {
                mShopDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //re-locks the shop menu
            }

            @Override
            public void onDrawerStateChanged(int i)
            {

            }
        });


//        Button hostButton = findViewById(R.id.testHostButton);
//        Button connectButton = findViewById(R.id.testConnectButton);
        Button commandButton = findViewById(R.id.commandButton);

//        hostButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                System.out.println("NETWORK: ATTEMPTING TO HOST");
//
//                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//                System.out.println("Fetched IP: " + ip);
//
//                Thread socketServerThread = new Thread(new SocketServerThread());
//                socketServerThread.start();
//                /*
//                async = new AsyncTask<Void, Void, Void>()
//                {
//                    @SuppressLint("WrongThread")
//                    @Override
//                    protected Void doInBackground(Void... params)
//                    {
//                        byte[] lMsg = new byte[4096];
//                        DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
//                        DatagramSocket ds = null;
//
//
//                        int serverPort = 8888;
//
//                        try
//                        {
//                            ss = new ServerSocket(serverPort);
//                            System.out.println("Port from server: " + ss.getLocalPort());
//
//                            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
//                            while (enumNetworkInterfaces.hasMoreElements())
//                            {
//                                NetworkInterface networkInterface = enumNetworkInterfaces
//                                        .nextElement();
//                                Enumeration<InetAddress> enumInetAddress = networkInterface
//                                        .getInetAddresses();
//                                while(enumInetAddress.hasMoreElements())
//                                {
//                                    InetAddress inetAddress = enumInetAddress.nextElement();
//
//                                    if(inetAddress.isSiteLocalAddress())
//                                    {
//                                        System.out.println("SiteLocalAddress: " + inetAddress.getHostAddress() + "\n");
//                                    }
//
//                                }
//                            }
//
//                            boolean fakebool = true;
//                            while(fakebool){
//                                Socket testSocket = ss.accept();
//                                System.out.println("connected to " + testSocket.getInetAddress() + ":" + testSocket.getPort());
//                            }
//
//                            ds = new DatagramSocket(serverPort, InetAddress.getByName("192.168.100.188"));
//                            System.out.println("NETWORK: DATAGRAM SOCKET OPENED AT PORT 8888");
//                            System.out.println("NETWORK: HOSTS INET ADDRESS: " + ds.getLocalSocketAddress());
//
//
//                            while(true)
//                            {
//                                ds.receive(dp);
//                                System.out.println("RECEIVED A PACKET HOLY SHIT");
//                                String recMssg = new String(lMsg,0,dp.getLength());
//                                System.out.println("Received packet content: " + recMssg);
//                                Intent i = new Intent();
//                                i.setAction("com.gutierrez.knightsduty.Sendbroadcast");
//                                i.putExtra("SEND", new String(lMsg, 0, dp.getLength()));
//                                getApplicationContext().sendBroadcast(i);
//                            }
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                        finally
//                        {
//                            if (ds != null)
//                            {
//                                ds.close();
//                            }
//                        }
//                        return null;
//                    }
//                };
//
//                async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                */
//            }
//        });

//        connectButton.setOnClickListener(new View.OnClickListener()
//        {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            public void onClick(View v)
//            {
//                System.out.println("NETWORK: ATTEMPTING TO CONNECT TO HOST");
//                final String mssg = "TEST";
//
//                MyClientTask clientTask = new MyClientTask("192.168.232.2", 8080);
//                clientTask.execute();
//                /*
//                async = new AsyncTask<Void, Void, Void>()
//                {
//                    @SuppressLint("WrongThread")
//                    @Override
//                    protected Void doInBackground(Void... voids)
//                    {
//                        DatagramSocket ds = null;
//
//                        try
//                        {
//                            System.out.println("Before new socket method");
//                            String response = "";
//                            Socket testSock = new Socket("192.168.200.2",8888);
//                            ByteArrayOutputStream byteArrayOutputStream =
//                                    new ByteArrayOutputStream(1024);
//                            byte[] buffer = new byte[1024];
//
//                            int bytesRead;
//                            InputStream inputStream = testSock.getInputStream();
//                            System.out.println("Right before while loop");
//                            while ((bytesRead = inputStream.read(buffer)) != -1){
//                                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                                response += byteArrayOutputStream.toString("UTF-8");
//                                System.out.println("did somethin");
//                                System.out.println(response);
//                            }
//
//
//
//                            ds = new DatagramSocket();
//                            ds.connect(InetAddress.getByName("47.144.34.110"), 8888);
//                            System.out.println("Is connected status: " + ds.isConnected());
//                            DatagramPacket dp;
//                            dp = new DatagramPacket(mssg.getBytes(), mssg.length(), InetAddress.getByName("47.144.34.110"), 8888);
//                            //ds.setBroadcast(true);
//                            ds.send(dp);
//                            System.out.println("NETWORK: DATAGRAM SHOULD HAVE BEEN SENT");
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                        finally
//                        {
//                            if (ds != null)
//                            {
//                                ds.close();
//                            }
//                        }
//                        return null;
//                    }
//                    protected void onPostExecute(Void result)
//                    {
//                        super.onPostExecute(result);
//                    }
//                };
//
//                async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
//            }
//        });


        commandButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mGLSurfaceView.toggleCommandMode();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mGLSurfaceView.onResume();

        //get content view and hide title and nav bar again
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mGLSurfaceView.onPause();
    }

    protected void showShop(ArrayList<ShopUnit> unitList, ArrayList<ShopUpgrade> availableUpgrades){
        ArrayList<ShopUnit> generalList = unitList;
        if(mAdapter == null){
            mAdapter = new ShopAdapter(generalList, this);
            mShopRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyDataSetChanged();
        }
        mShopDrawer.openDrawer(GravityCompat.END);
        //populate drawer contents with info from unitList and upgradeList

    }

    public void notifyUnitPurchase(int position){
        mGLSurfaceView.notifyUnitPurchase(position);
    }

    public void updateGoldDisplay(float gold)
    {
        mGoldDisplay.setText(Integer.toString((int)Math.floor(gold)));
    }

    public void updateHealthDisplay(int health)
    {
        mHealthDisplay.setText(Integer.toString((health)));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////NETWORK FUNCTIONS///////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////SERVER THREAD///////////////////////////////////////////////////////
    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                ss = new ServerSocket(SocketServerPORT);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("I'm waiting here: "+ ss.getLocalPort());
                    }
                });

                while (true) {
                    Socket socket = ss.accept();
                    count++;
                    System.out.println("#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n");

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //msg.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Android, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                System.out.println("replayed: " + msgReply);

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Something wrong! " + e.toString() + "\n");
            }

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //msg.setText(message);
                }
            });
        }

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////CLIENT THREAD///////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println(response);
            super.onPostExecute(result);
        }

    }
}
