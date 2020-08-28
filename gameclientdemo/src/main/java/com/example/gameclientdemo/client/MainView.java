package com.example.gameclientdemo.client;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.example.commondemo.base.Command;
import com.example.commondemo.base.RequestCode;
import com.example.commondemo.base.TcpProtocol;
import com.example.commondemo.message.Message;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class MainView extends JFrame {
    public static final JTextPane  OUTPUT = new JTextPane();
    private static final JTextArea INPUT = new JTextArea();
    public static final JTextArea SKILL = new JTextArea();
    public static final String SKILL_INFO="技能信息:\n";
    public static final JTextArea INFORMATION = new JTextArea();
    public static final String PLAYER_INFO="角色信息:\n";
    /** 地图界面 */
    public static final  JTextArea MAP = new JTextArea();
    public static final String PLACE="位置:\n";
    public static final JTextArea EQUIPMENT = new JTextArea();
    public static final String EQU="装备栏:\n";
    public static final JTextArea BAG = new JTextArea();
    public static final String BAG_INFO="背包栏:\n";
    /**敌人信息*/
    public static final JTextArea TARGET  = new JTextArea();
    public static final String FORMAT="***************************";
    public static void outputAppend(Integer type,String string){
        Integer size=15;
        Color color=Color.BLUE;
        if(type.equals(RequestCode.BAD_REQUEST.getCode())){
            size=21;
            color=Color.RED;
        }else if(type.equals(RequestCode.WARNING.getCode())){
            size=18;
            color=Color.ORANGE;
        }
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, color);
        StyleConstants.setFontSize(attrSet, size);
        Document doc = MainView.OUTPUT.getDocument();
        try {
            doc.insertString(doc.getLength(), string+"\n", attrSet);
        } catch (BadLocationException e) {
            System.out.println("BadLocationException:   " + e);
        }
    }

    public MainView() {
        this.setLayout(null);

        SKILL.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        SKILL.setLineWrap(true);
        SKILL.setText(SKILL_INFO+"角色尚未登陆");

        INPUT.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        INPUT.setLineWrap(true);
        INPUT.setText("请在此处输入命令");

        INFORMATION.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        INFORMATION.setLineWrap(true);
        INFORMATION.setText(PLAYER_INFO+" 角色尚未登陆");

        MAP.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        MAP.setLineWrap(true);
        MAP.setText(PLACE+"未知地点");

        EQUIPMENT.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        EQUIPMENT.setLineWrap(true);
        EQUIPMENT.setText(EQU+"角色尚未登陆");

        BAG.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        BAG.setLineWrap(true);
        BAG.setText(BAG_INFO+"角色尚未登陆");

        TARGET.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 18));
        TARGET.setLineWrap(true);
        TARGET.setText(FORMAT+"未进行攻击"+FORMAT);

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
                    SimpleAttributeSet attrSet = new SimpleAttributeSet();
                    StyleConstants.setForeground(attrSet, Color.BLACK);
                    StyleConstants.setFontSize(attrSet, 15);
                    Document doc = MainView.OUTPUT.getDocument();
                    String s = text+ "\n";
                    try {
                        doc.insertString(doc.getLength(), s, attrSet);
                    } catch (BadLocationException exception) {
                        System.out.println("BadLocationException:   " + exception);
                    }
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

        JScrollPane informationBar = new JScrollPane(INFORMATION);
        informationBar.setBounds(10,5,300, 805);

        // 目标信息栏
        JScrollPane targetBar = new JScrollPane(TARGET);
        targetBar.setBounds(320,5,700, 30);

        JScrollPane displayBox = new JScrollPane(OUTPUT);

        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        displayBox.setBounds(320, 40, 700, 645);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        displayBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //在文本框上添加滚动条
        JScrollPane inputBox = new JScrollPane(INPUT);
        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        inputBox.setBounds(320, 695, 700, 115);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        inputBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 角色信息栏
        JScrollPane skillBar = new JScrollPane(SKILL);
        skillBar.setBounds(1024,5,380, 370);

        // 地图
        JScrollPane mapBar = new JScrollPane(MAP);
        mapBar.setBounds(1024,380,380,50);

        // 装备栏
        JScrollPane equipmentBar = new JScrollPane(EQUIPMENT);
        equipmentBar.setBounds(1024, 435, 380, 170);

        // 背包物品栏
        JScrollPane bagsBar = new JScrollPane(BAG);
        bagsBar.setBounds(1024, 610, 380, 200);


        //把滚动条添加到容器里面
        this.add(targetBar);
        this.add(displayBox);
        this.add(inputBox);
        this.add(informationBar);
        this.add(mapBar);
        this.add(equipmentBar);
        this.add(bagsBar);
        this.add(skillBar);


        this.setSize(1440, 860);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
