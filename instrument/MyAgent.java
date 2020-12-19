package instrument;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.lang.instrument.IllegalClassFormatException;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import java.io.ByteArrayInputStream;

public class MyAgent {

	// Instrumentation instance is passed from JVM system
	public static void premain(String agentArgs, Instrumentation instrumentation) {

		// Register Bytecode Transformer through Instrumentation instance
		// JVM will call Transformer every time when JVM loads new classes
		instrumentation.addTransformer(new ClassFileTransformer() {

			// transform function can read and modify loaded classed definitions
			public byte[] transform(ClassLoader loader,
				String className,
				Class<?> classBeingRedefined,
				ProtectionDomain protectionDomain,
				byte[] classfileBuffer) {

				byte[] byteCode = classfileBuffer;

				// apply BCI to specific classes
				// This example uses Javassist byte code engineering library
				if (className.equals("instrument/HelloWorld")) {

					try {
						// Create a new class definition based on the original class
						ClassPool classPool = ClassPool.getDefault();
						CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
							classfileBuffer));
						CtMethod[] methods = ctClass.getDeclaredMethods();

						for (CtMethod method : methods) {
							if (method.getName().equals("sayHello")) {

								// Inject Bytecode before sayHello method defintiion
								// $1 is function's first parameter
								method.insertBefore("$1=\"Mallory\";");
							}
						}

						// Transform this class definition into bytecodes
						byteCode = ctClass.toBytecode();
						ctClass.detach();

					} catch (Throwable ex) {
						System.out.println("Exception: " + ex);
						ex.printStackTrace();
					}

					System.out.println("=== Javaバイトコードが変更されました ===");
				}

				return byteCode;
			}
		});
	}
}