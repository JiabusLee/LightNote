package com.simple.lightnote.model;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;

public class Test {
	void test() {
		String s = "a69e1dfbfef04cfa12aeaeeb16a8d71cf59c01bc549afb5604b2d77b48dbd9d7e047ab237af77f066e49735fecd6d2ed6ab0b7cfb4d4793dd3605acac26b924577719f9a483c6d7f4627c09ede4f2eadef7596a3d32c3ff303986e257f695ccd9cd3e0ad72d1615672be7805121131e2b2f508378ed957fe6615792a01801007408b87be8dab9c22e93ee632dd01cd78f1010e788f35b274bda3b07f260af63262a161bcbb7259ff843e0d9ae5d44f9de3fc3df6fc7ea3f25030d52559386f464584810e78bd7a3f7398b88409c0d50a7ca91e0845f4ce0ddf20c2afe4095410570a30b75270c9c9bcfd83ac9d0181c5e613cde4ad008b50e7583f17f065b1db60493214188e2617ae86996fdd10bed548c242339eb1943d5f9cf898f1d9e819d742b89512a15ecfc7fb293116842e51a5f2db390430959f801a179c9cbb5cd16459652a7a19a5718dcf6724f6148711b63fda8b4006ab00294f7ad63806ff0521b701b77b4db70bfd93052b5ad5e5bef9628f9e973487a32fcf4182e8a669f101e82d75469a30f5b2e31cf89391e540c914267ce1058fd95ee10c708288037a8258fa2d766581166c8c0436f179fbfd3dc46f601a5eca9f88131289ae7715a7727cd8a09e9b39e640fe6721215ef333aa59d872366a9a7abe97c4878d2143ecff9481379a4151db463f7c05b1d709e6d22992c2bb76299025f8af9d0dc17b023608e52c3ab1f6c979eabfcb05279a3d49660e35ae9f9c3cb4ca65c5add2472fcfb3da2b09036e9f6022e5400a09168783697a29028bc03d";
		String s1 = "a69e1dfbfef04cfa12aeaeeb16a8d71cf59c01bc549afb5604b2d77b48dbd9d71618bc7d2f5e368ad06de2d2430e5255f1fed66dae73c5f61c38dc7ff089d8c65177d432ef61f5d4";
		String str = DESEncrypt.decrypt(s);
		System.out.println(str);
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		StringBuffer sb=new StringBuffer();
		String s="";
		while((s=scanner.nextLine())!=null&&s.length()>0){
			sb.append(s);
		}
		String str = DESEncrypt.decrypt(sb.toString());
		System.out.println("解密的数据:"+str);

		// new Test().test();
	}
}
