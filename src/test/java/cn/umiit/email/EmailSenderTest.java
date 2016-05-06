package cn.umiit.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.paraller.email.EmailContent;
import net.paraller.email.EmailSender;
import net.paraller.email.PlaceHolderUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class EmailSenderTest extends TestCase
{
	
	private String username = "noreply@yeamoney.cn";
	private String wrongUsername = "noreply2@yeamoney.cn";
	private String password = "XXX";
	private String fromAddress = "noreply@yeamoney.cn";
	private String serverHost = "smtp.mxhichina.com";
	private String serverPort = "25";
	private String isAuth = "true";
	private String nickname = "金融";

	private String subject = "这是主题 ";
	private String content = "这是正文";
	private String toAddress1 = "422351001@qq.com";
	private String toAddress2 = "zhida.liao@yeamoney.cn";
	
	private String templateContent = "恭喜您成功注册，你的邀请码为${code}";
	private String result = "恭喜您成功注册，你的邀请码为123456";
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EmailSenderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EmailSenderTest.class );
    }

    private Properties getP(){
    	Properties pro = new Properties();
    	pro.setProperty("mail.smtp.port", serverPort);
    	pro.setProperty("mail.smtp.username",username );
    	pro.setProperty("mail.smtp.password",password );
    	pro.setProperty("mail.smtp.from",fromAddress );
    	pro.setProperty("mail.smtp.host", serverHost);
    	pro.setProperty("mail.smtp.auth", isAuth);
    	pro.setProperty("mail.smtp.nickname", nickname);
    	
    	return pro;
    }

    /**
     * 装载配置文件失败
     */
    public void testApp0()
    {
    	Properties pro = new Properties();
    	pro.setProperty("mail.smtp.port", serverPort);
    	pro.setProperty("mail.smtp.username",username );
    	pro.setProperty("mail.smtp.password",password );
    	pro.setProperty("mail.smtp.from",fromAddress );
    	pro.setProperty("mail.smtp.host", serverHost);
    	
    	boolean flag = true;
    	EmailSender emailSender = new EmailSender();
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			flag = false;
		}
    	assertEquals(false, flag);
    }
    
    /**
     * 装载配置文件失败
     */
    public void testApp01()
    {
    	Properties pro = getP();

    	boolean flag = true;
    	EmailSender emailSender = new EmailSender();
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
    	assertEquals(true, flag);
    }
    
    /**
     * 发送 一个邮件
     * 正常情况
     */
    public void testApp1()
    {
    	Properties pro = getP();

    	EmailSender emailSender = new EmailSender();
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	assertEquals(EmailSender.SUCC_STATE, emailSender.singleEmail(toAddress1, subject, content));
    }
    
    /**
     * 发送 一个邮件 带有模版的
     * 正常情况
     */
    public void testApp11()
    {
    	Properties pro = getP();

    	pro.setProperty("mail.template.zhuce", templateContent);
    	
    	EmailSender emailSender = new EmailSender();
    	Map<String, String> parameter = new HashMap<String,String>();
    	parameter.put("code", "123456");
    	
    	assertEquals(PlaceHolderUtils.resolvePlaceholders(templateContent, parameter), result);
    	
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	assertEquals(EmailSender.SUCC_STATE, emailSender.singleTemplateEmail("zhuce",parameter, toAddress1, subject, content));
    }
    
    /**
     * 发送 一个邮件
     * 失败情况
     */
    public void testApp2()
    {
    	Properties pro = getP();
    	pro.setProperty("mail.smtp.username",wrongUsername );

    	
    	EmailSender emailSender = new EmailSender();
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	assertEquals(EmailSender.FAIL_STATE, emailSender.singleEmail(toAddress1, subject, content));
    	
    }
    
    /**
     * 简单的群发邮件 正常情况
     */
    public void testApp3()
    {
    	Properties pro = getP();
    	EmailSender emailSender = new EmailSender();
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	List<String> lists = new ArrayList<String>();
    	lists.add(toAddress1);
    	lists.add(toAddress2);
    	
    	assertEquals(EmailSender.SUCC_STATE, emailSender.simpleBatchEmail(lists, subject, content) );
    	
    }
    
    /**
     * 群发邮件 正常情况
     */
    public void testApp4()
    {
    	Properties pro = getP();
    	EmailSender emailSender = new EmailSender();
    	List<EmailContent> lists = new ArrayList<EmailContent>();
    	EmailContent e1 = new EmailContent();
    	EmailContent e2 = new EmailContent();
    	e1.setContent(content);
    	e2.setContent(content);
    	e1.setSubject(subject);
    	e2.setSubject(subject);
    	e1.setToAddress(toAddress1);
    	e2.setToAddress(toAddress2);
    	lists.add(e2);
    	lists.add(e1);
    	
    	try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	assertEquals(EmailSender.SUCC_STATE, emailSender.BatchEmail(lists));
    }
    
}
