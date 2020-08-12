package com.example.gameclientdemo.client;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.base.Command;
import com.example.commondemo.base.TcpProtocol;
import com.example.commondemo.message.Message;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class MainView extends JFrame {

    public static final JTextArea OUTPUT = new JTextArea();
    private static final JTextArea INPUT = new JTextArea();
    public static final JTextArea INFORMATION = new JTextArea();
    /** 地图界面 */
    public static final  JTextArea MAP = new JTextArea();
    public static final JTextArea EQUIPMENT = new JTextArea();
    public static final JTextArea BAG = new JTextArea();

    public MainView() {
        this.setLayout(null);

        OUTPUT.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 16));
        OUTPUT.setLineWrap(true);

        INPUT.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        INPUT.setLineWrap(true);
        INPUT.setText("请在此处输入命令");

        INFORMATION.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 16));
        INFORMATION.setLineWrap(true);
        INFORMATION.setText("角色信息：\n 角色尚未登陆");

        MAP.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        MAP.setLineWrap(true);
        MAP.setText("位置 ：未知地点\n");

        EQUIPMENT.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        EQUIPMENT.setLineWrap(true);
        EQUIPMENT.setText("装备栏：\n 角色尚未登陆");

        BAG.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        BAG.setLineWrap(true);
        BAG.setText("背包栏：\n 角色尚未登陆");


        INPUT.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // NOOP
            }

            @SneakyThrows
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == '\n') {
                    String text = INPUT.getText().replaceAll("\n", "");
                    //回显框回显
                    OUTPUT.append(text + "\n");
                    System.out.println("客户端输入： " + text);
                    String[] array = text.split("\\s+");
                    Command byCommand = Command.findByCommand(array[0], Command.UNKNOWN);
                    Message message = new Message();
                    message.setRequestCode(byCommand.getRequestCode());
                    message.setMessage(text);
                    System.out.println(message.toString());
                    byte[] encode = ProtobufProxy.create(Message.class).encode(message);
                    TcpProtocol protocol=new TcpProtocol();
                    protocol.setData(encode);
                    protocol.setLen(encode.length);
                    TcpClient.channel.writeAndFlush(protocol);
                    INPUT.setText("");

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == '\n') {
                    INPUT.setCaretPosition(0);
                }
            }
        });


        JScrollPane displayBox = new JScrollPane(OUTPUT);

        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        displayBox.setBounds(0, 0, 1000, 680);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        displayBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //在文本框上添加滚动条
        JScrollPane inputBox = new JScrollPane(INPUT);
        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        inputBox.setBounds(0, 690, 1000, 90);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        inputBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 角色信息栏
        JScrollPane informationBar = new JScrollPane(INFORMATION);
        informationBar.setBounds(1024,0,1368, 320);

        // 地图
        JScrollPane mapBar = new JScrollPane(MAP);
        mapBar.setBounds(1024,325,1368,75);

        // 装备栏
        JScrollPane equipmentBar = new JScrollPane(EQUIPMENT);
        equipmentBar.setBounds(1024, 400, 1368, 200);

        // 背包物品栏
        JScrollPane bagsBar = new JScrollPane(BAG);
        bagsBar.setBounds(1024, 600, 1368, 200);


        //把滚动条添加到容器里面
        this.add(displayBox);
        this.add(inputBox);
        this.add(informationBar);
        this.add(mapBar);
        this.add(equipmentBar);
        this.add(bagsBar);


        this.setSize(1440, 860);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
