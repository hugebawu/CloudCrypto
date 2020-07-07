/**
 * 
 */
package cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE;

import java.math.BigInteger;

import org.bouncycastle.crypto.InvalidCipherTextException;

import cn.edu.ncepu.crypto.HE.ibeHE.IBEHEEngine;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.generators.BF01aHEDecryptionGenerator;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.generators.BF01aHEEncryptionGenerator;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.generators.BF01aHEKeyPairGenerator;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.generators.BF01aHESecretKeyGenerator;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.serparams.BF01aHECiphertextSerParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.serparams.BF01aHEMasterSecretKeySerParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.serparams.BF01aHEPublicKeySerParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.bf01aHE.serparams.BF01aHESecretKeySerParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.genparams.IBEHEDecryptionGenerationParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.genparams.IBEHEEncryptionGenerationParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.genparams.IBEHEKeyPairGenerationParameter;
import cn.edu.ncepu.crypto.HE.ibeHE.genparams.IBEHESecretKeyGenerationParameter;
import cn.edu.ncepu.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.ncepu.crypto.algebra.serparams.PairingKeySerPair;
import cn.edu.ncepu.crypto.algebra.serparams.PairingKeySerParameter;
import cn.edu.ncepu.crypto.utils.PairingUtils;
import it.unisa.dia.gas.jpbc.PairingParameters;

/**
 * @Copyright : Copyright (c) 2020-2021 
 * @author: Baiji Hu
 * @E-mail: drbjhu@163.com
 * @Version: 1.0
 * @CreateData: Jul 7, 2020 12:06:00 PM
 * @ClassName BF01aHEEngine
 * @Description: TODO(这里用一句话描述这个方法的作用)
 */
public class BF01aHEEngine extends IBEHEEngine {
	// Scheme name, used for exceptions
	private static final String SCHEME_NAME = "Boneh-Franklin CPA-secure IBE based homomorphic encryption scheme";
	private static BF01aHEEngine engine;

	public static BF01aHEEngine getInstance() {
		if (null == engine) {
			engine = new BF01aHEEngine();
		}
		return engine;
	}

	private BF01aHEEngine() {
		super(SCHEME_NAME, ProveSecModel.RandomOracle, PayloadSecLevel.CPA, PredicateSecLevel.ANON);
	}

	@Override
	protected PairingKeySerPair setup(PairingParameters pairingParameters) {
		BF01aHEKeyPairGenerator keyPairGenerator = new BF01aHEKeyPairGenerator();
		keyPairGenerator.init(new IBEHEKeyPairGenerationParameter(pairingParameters));
		return keyPairGenerator.generateKeyPair();
	}

	@Override
	protected PairingKeySerParameter extract(String id, PairingKeySerParameter masterKey) {
		if (!(masterKey instanceof BF01aHEMasterSecretKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, masterKey,
					BF01aHEMasterSecretKeySerParameter.class.getName());
		}
		BF01aHESecretKeyGenerator secretKeyGenerator = new BF01aHESecretKeyGenerator();
		secretKeyGenerator.init(new IBEHESecretKeyGenerationParameter(null, masterKey, id));
		return secretKeyGenerator.generateKey();
	}

	@Override
	protected PairingCipherSerParameter encrypt(String id, PairingKeySerParameter publicKey, BigInteger biMessage)
			throws InvalidCipherTextException {
		if (!(publicKey instanceof BF01aHEPublicKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, publicKey,
					BF01aHEPublicKeySerParameter.class.getName());
		}
		BF01aHEEncryptionGenerator encryptionGenerator = new BF01aHEEncryptionGenerator();
		encryptionGenerator.init(new IBEHEEncryptionGenerationParameter(id, publicKey, biMessage));
		return encryptionGenerator.generateCiphertext();
	}

	@Override
	protected BigInteger decrypt(PairingKeySerParameter secretKey, PairingCipherSerParameter ciphertext)
			throws InvalidCipherTextException {

		if (!(secretKey instanceof BF01aHESecretKeySerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, secretKey,
					BF01aHESecretKeySerParameter.class.getName());
		}
		if (!(ciphertext instanceof BF01aHECiphertextSerParameter)) {
			PairingUtils.NotVerifyCipherParameterInstance(SCHEME_NAME, ciphertext,
					BF01aHECiphertextSerParameter.class.getName());
		}
		BF01aHEDecryptionGenerator decryptionGenerator = new BF01aHEDecryptionGenerator();
		decryptionGenerator.init(new IBEHEDecryptionGenerationParameter(null, secretKey, ciphertext));
		return decryptionGenerator.recoverMessage().toBigInteger();
	}

}
