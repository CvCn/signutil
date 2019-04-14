import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class mainwindow {
    private JPanel mainPanel;
    private JTextArea paramsField;
    private JCheckBox timeStamp;
    private JCheckBox randStr;
    private JTextArea addParams;
    private JTextField sign;
    private JButton action;


    private boolean timeStampSelect = true;
    private boolean randStrSelect = true;

    public static void main(String[] args) {
        JFrame mainwindow = new JFrame("MD5签名生成工具");
        mainwindow.setContentPane(new mainwindow().mainPanel);
        mainwindow.setLocation(600, 300);
        mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainwindow.setMaximumSize(new Dimension(800, 200));
        mainwindow.pack();
        mainwindow.setVisible(true);
//        mainwindow.setResizable(false);
    }


    public mainwindow(){
        spliceParams(paramsField.getText(), timeStampSelect, randStrSelect);

//        addParams.setLineWrap(true);


        this.paramsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                spliceParams(paramsField.getText(), timeStampSelect, randStrSelect);
            }
        });

        this.timeStamp.addActionListener(e -> {
            timeStampSelect = timeStamp.isSelected();
            spliceParams(paramsField.getText(), timeStampSelect, randStrSelect);
        });

        this.randStr.addActionListener(e->{
            randStrSelect = randStr.isSelected();
            spliceParams(paramsField.getText(), timeStampSelect, randStrSelect);
        });

        this.action.addActionListener((e)->{
            sign.setText(md5(addParams.getText()));
        });
    }


    /**
     * 拼接参数
     * @param params
     * @param timeStampSelect
     * @param randStrSelect
     */
    private void spliceParams(String params, boolean timeStampSelect, boolean randStrSelect){

        //把参数按&分割
        String[] split = params.split("&");

        //把参数格式化成map
        TreeMap<String, String> paramsMap = new TreeMap<>();
        for(String s : split){
            String[] param = s.split("=");
            if(param.length == 2){
                paramsMap.put(param[0], param[1]);
            }
        }

        //判断是否拼接时间戳和随机字符串的逻辑
        if(timeStampSelect && randStrSelect){
            paramsMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            paramsMap.put("randStr", UUID.randomUUID().toString().replace("-", ""));
        }else if(!timeStampSelect && randStrSelect){
            paramsMap.remove("timeStamp");
            paramsMap.put("randStr", UUID.randomUUID().toString().replace("-", ""));
        }else if(timeStampSelect && !randStrSelect){
            paramsMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            paramsMap.remove("randStr");
        }else{
            paramsMap.remove("randStr");
            paramsMap.remove("timeStamp");
        }


        String re = "";

        Set<String> paramKeys = paramsMap.keySet();

        //将参数按字典序重新拼接
        for(String key : paramKeys){
            re += key + "=" + paramsMap.get(key) + "&";
        }
        if(paramKeys.size() > 0){
            re = re.substring(0, re.length()-1);
        }
        //输出
        this.addParams.setText(re);
    }


    private String md5(String s){
        //定义一个字节数组
        byte[] secretBytes;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(s.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
