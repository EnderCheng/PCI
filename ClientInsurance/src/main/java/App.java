/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.SerializationUtils;
import org.hyperledger.fabric.gateway.*;
import org.miracl.core.BN254.BIG;
import org.miracl.core.BN254.ECP;
import org.miracl.core.BN254.ROM;
import org.miracl.core.RAND;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static java.lang.System.exit;


public class App {
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "fabric-samples", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

//		Gateway.Builder builder = Gateway.createBuilder().commitHandler(NoDelayCommitHandlerFactory.INSTANCE);
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {

		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		int max_repeat = 1;
		double max_init = 0, max_dep = 0, max_rec= 0, max_eval= 0, max_aud= 0,
				max_auth= 0, max_insp= 0, max_cfm= 0, max_quit= 0;
		double min_init= 99999, min_dep= 99999, min_rec= 99999, min_eval= 99999, min_aud= 99999,
				min_auth= 99999, min_insp= 99999, min_cfm= 99999, min_quit= 99999;
		double avg_init= 0, avg_dep= 0, avg_rec= 0, avg_eval= 0, avg_aud= 0,
				avg_auth= 0, avg_insp= 0, avg_cfm= 0, avg_quit= 0;
		double pre = Math.pow(10,6);

		int Deposit_comm = 0;
		int Deposit_store = 0;
		int Record_comm = 0;
		int Record_store = 0;
		int Evalute_comm = 0;
		int Evalute_store = 0;
		int Audit_comm = 0;
		int Audit_store = 0;
		int Authorize_comm = 0;
		int Authorize_store = 0;
		int Inspect_comm = 0;
		int Inspect_store = 0;
		int Confirm_comm = 0;
		int Confirm_store = 0;
		int Quit_comm = 0;
		int Quit_store = 0;
		try (Gateway gateway = connect()) {

			for(int repeat = 0;repeat<max_repeat;repeat++) {
				//local data process (driver)
				BigInteger[] x = new BigInteger[Parameters.nlength - 1];
				for (int i = 0; i < Parameters.nlength - 1; i++) {
					x[i] = Utils.randomRange(Parameters.range_x_1, Parameters.range_x_2);
				}
				BigInteger random = Utils.randomRange(Parameters.range_r_1, Parameters.range_r_2);
				BigInteger v = Utils.randomZbits(Utils.tau + Utils.Nlength);
				BigInteger c = Parameters.g.modPow(random, Parameters.N).multiply(Parameters.h.modPow(v, Parameters.N)).mod(Parameters.N);

				BigInteger a = Utils.randomRange(Parameters.range_a_1, Parameters.range_a_2);
				BigInteger b = Utils.randomRange(Parameters.range_b_1, Parameters.range_b_2);

				BigInteger r_a = random.multiply(a);
				BigInteger v_a = v.multiply(a);

				BigInteger E_x = BigInteger.ONE;
				for (int i = 0; i < Parameters.nlength - 1; i++) {
					E_x = E_x.multiply(Parameters.W[i].modPow(x[i], Parameters.Nsquare));
				}

				E_x = E_x.multiply(Parameters.W[Parameters.nlength - 1])
						.multiply(Parameters.gg.modPow(random, Parameters.Nsquare)).mod(Parameters.Nsquare);

				BigInteger E_x_a = E_x.modPow(a, Parameters.Nsquare)
						.multiply(Parameters.gg.modPow(r_a.negate(), Parameters.Nsquare))
						.multiply(Parameters.gg.modPow(b, Parameters.Nsquare)).mod(Parameters.Nsquare);
				Paillier cipher = new Paillier(Utils.Nlength, Parameters.p, Parameters.q, Parameters.lambda, Parameters.N, Parameters.Nsquare, Parameters.gg, Parameters.mu);
				PaillierProof pproof = new PaillierProof();
				ReportProofParameters rpp = pproof.prove_driving_record(Parameters.g, Parameters.h, cipher, c,
						random, v, a, b, r_a, v_a, E_x, E_x_a, x, Parameters.W,
						Parameters.range_a_1, Parameters.range_a_2, Parameters.range_b_1, Parameters.range_b_2, Parameters.range_r_1,
						Parameters.range_r_2, Parameters.range_x_1, Parameters.range_x_2);
				BIG m = Utils.BigInteger_to_BIG(new BigInteger("123456"));
				AES aes = new AES(Utils.getKey(Parameters.g_.mul(m)), Utils.getIV());
				byte[] text = Utils.fillBlock(Base64.getEncoder().encodeToString(SerializationUtils.serialize(x))).getBytes(StandardCharsets.UTF_8);
				byte[] ctt = aes.CBC_encrypt(text);

				ReportMessage rm = new ReportMessage(E_x_a, E_x, c, rpp, ctt);
				Asset asset = new Asset(3000);

				ObjectMapper mapper = new ObjectMapper();
				mapper.setPropertyNamingStrategy(new MyNamingStrategy());
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				String json_rm = mapper.writeValueAsString(rm);
				String json_asset = mapper.writeValueAsString(asset);

				//local data process (company)
				BigInteger alpha = Utils.randomRange(Parameters.range_alpha_1, Parameters.range_alpha_2);
				BigInteger beta = Utils.randomRange(Parameters.range_beta_1, Parameters.range_beta_2);

				BigInteger E_cipher = E_x_a.modPow(alpha, Parameters.Nsquare)
						.multiply(Parameters.gg.modPow(beta, Parameters.Nsquare)).mod(Parameters.Nsquare);
				BigInteger mm = cipher.decrypt(E_cipher);
				BigInteger Gamma = Utils.randomZStarN(Utils.Nlength, Parameters.N);
				BigInteger U = Parameters.gg.modPow(mm, Parameters.Nsquare).multiply(Gamma.modPow(Parameters.N, Parameters.Nsquare)).mod(Parameters.Nsquare);
				BigInteger D = E_cipher.multiply(U.modPow(BigInteger.ONE.negate(), Parameters.Nsquare)).mod(Parameters.Nsquare);
				BigInteger phi_N = cipher.getP().subtract(BigInteger.ONE).multiply(cipher.getQ().subtract(BigInteger.ONE));
				BigInteger exp_tmp = Parameters.N.modPow(BigInteger.ONE.negate(), phi_N);
				BigInteger Z = D.modPow(exp_tmp, Parameters.N);

				RecordProofParameters repp = pproof.prove_driving_safety(
						Parameters.g, Parameters.h, cipher, Z, E_cipher, E_x_a, U, Gamma, alpha, beta,
						Parameters.range_alpha_1, Parameters.range_alpha_2, Parameters.range_beta_1, Parameters.range_beta_2);

				ConfirmMessage cm = new ConfirmMessage(U, E_cipher, mm, repp);
				String json_cm = mapper.writeValueAsString(cm);

				//local auth process (Server)
				AuditMessage am = new AuditMessage(1);
				String json_am = mapper.writeValueAsString(am);

				//local authorize process(Driver)
				Elgamal elgamal = new Elgamal();
				elgamal.setParams(Parameters.order, Parameters.x, Parameters.g_, Parameters.h_, Parameters.bits);
				BIG r = BIG.randomnum(Parameters.order, elgamal.getRng());
				ECP[] ct = elgamal.encrypt(m, r);
				ElgamalProofParameters epp = elgamal.prove(m, r, ct);
				byte[] ct_1 = new byte[33];
				byte[] ct_2 = new byte[33];
				ct[0].toBytes(ct_1, true);
				ct[1].toBytes(ct_2, true);
				AuthorizeMessage authmsg = new AuthorizeMessage(1, ct_1, ct_2, epp);
				String json_authm = mapper.writeValueAsString(authmsg);

				// get the network and contract
				Network network = gateway.getNetwork("mychannel");
				Contract contract = network.getContract("InsuranceContract");

				//local inspect process (TPA)
				InspectMessage im = new InspectMessage(true);
				String json_im = mapper.writeValueAsString(im);

				//local confirm process (Server)
				NoAuditMessage nam = new NoAuditMessage("confirm");
				String json_nam = mapper.writeValueAsString(nam);

				//local quit process (Driver)
				QuitMessage qm = new QuitMessage("quit");
				String json_qm = mapper.writeValueAsString(qm);

				byte[] result;

				System.out.println("Submit Transaction: InitLedger");
				long time1 = System.nanoTime();
				result = contract.submitTransaction("InitLedger");
				long time2 = System.nanoTime();
				System.out.println("InitLedger time:" + (time2 - time1));
				double init_time = (time2 - time1)/pre;
				if(init_time>max_init)
					max_init = init_time;
				if(init_time<min_init)
					min_init = init_time;
				avg_init = avg_init+init_time;


				System.out.println("Submit Transaction: Deposit");
				long time3 = System.nanoTime();
				result = contract.submitTransaction("Deposit", json_asset);
				long time4 = System.nanoTime();
				System.out.println(time4 - time3);
				System.out.println("Deposit result:" + new String(result, StandardCharsets.UTF_8));
				double dep_time = (time4 - time3)/pre;
				if(dep_time>max_dep)
					max_dep = dep_time;
				if(dep_time<min_dep)
					min_dep = dep_time;
				avg_dep = avg_dep+dep_time;
				Deposit_comm = json_asset.getBytes(StandardCharsets.UTF_8).length;
				Deposit_store = 12;

				long time5 = System.nanoTime();
				result = contract.submitTransaction("Record", json_rm);
				long time6 = System.nanoTime();
				System.out.println(time6 - time5);
				System.out.println("Record result:" + new String(result, StandardCharsets.UTF_8));
				double record_time = (time6 - time5)/pre;
				if(record_time>max_rec)
					max_rec = record_time;
				if(record_time<min_rec)
					min_rec = record_time;
				avg_rec = avg_rec+record_time;
				Record_comm = json_rm.getBytes(StandardCharsets.UTF_8).length;
				Record_store = rm.getAudit_data().length+rm.getE_x_a().toByteArray().length;


				long time7 = System.nanoTime();
				result = contract.submitTransaction("Evaluate", json_cm);
				long time8 = System.nanoTime();
				System.out.println(time8 - time7);
				System.out.println("Evaluate result:" + new String(result, StandardCharsets.UTF_8));
				double eval_time = (time8 - time7)/pre;
				if(eval_time>max_eval)
					max_eval = eval_time;
				if(eval_time<min_eval)
					min_eval = eval_time;
				avg_eval = avg_eval+eval_time;
				Evalute_comm = json_cm.getBytes(StandardCharsets.UTF_8).length;
				Evalute_store = 4+4+4+4;

				long time9 = System.nanoTime();
				result = contract.submitTransaction("Audit", json_am);
				long time10 = System.nanoTime();
				System.out.println(time10 - time9);
				System.out.println("Audit result:" + new String(result, StandardCharsets.UTF_8));
				double audit_time = (time10 - time9)/pre;
				if(audit_time>max_aud)
					max_aud = audit_time;
				if(audit_time<min_aud)
					min_aud = audit_time;
				avg_aud = avg_aud+audit_time;
				Audit_comm = json_am.getBytes(StandardCharsets.UTF_8).length;
				Audit_store = 8;

				long time11 = System.nanoTime();
				result = contract.submitTransaction("Authorize", json_authm);
				long time12 = System.nanoTime();
				System.out.println(time12 - time11);
				System.out.println("Authorize result:" + new String(result, StandardCharsets.UTF_8));
				double auth_time = (time12 - time11)/pre;
				if(auth_time>max_auth)
					max_auth = auth_time;
				if(auth_time<min_auth)
					min_auth = auth_time;
				avg_auth = avg_auth+auth_time;
				Authorize_comm = json_authm.getBytes(StandardCharsets.UTF_8).length;
				Authorize_store = authmsg.getCt_1().length+authmsg.getCt_2().length+4;

				long time13 = System.nanoTime();
				result = contract.submitTransaction("Inspect", json_im);
				long time14 = System.nanoTime();
				System.out.println(time14 - time13);
				System.out.println("Inspect result:" + new String(result, StandardCharsets.UTF_8));
				double inspect_time = (time14 - time13)/pre;
				if(inspect_time>max_insp)
					max_insp = inspect_time;
				if(inspect_time<min_insp)
					min_insp = inspect_time;
				avg_insp = avg_insp+inspect_time;
				Inspect_comm = json_im.getBytes(StandardCharsets.UTF_8).length;
				Inspect_store = 8;

				long time15 = System.nanoTime();
				result = contract.submitTransaction("Confirm", json_nam);
				long time16 = System.nanoTime();
				System.out.println(time16 - time15);
				System.out.println("Confirm result:" + new String(result, StandardCharsets.UTF_8));
				double confirm_time = (time16 - time15)/pre;
				if(confirm_time>max_cfm)
					max_cfm = confirm_time;
				if(confirm_time<min_cfm)
					min_cfm = confirm_time;
				avg_cfm = avg_cfm+confirm_time;
				Confirm_comm= json_nam.getBytes(StandardCharsets.UTF_8).length;
				Confirm_store = 8;

				long time17 = System.nanoTime();
				result = contract.submitTransaction("Quit", json_qm);
				long time18 = System.nanoTime();
				System.out.println(time18 - time17);
				System.out.println("Quit result:" + new String(result, StandardCharsets.UTF_8));
				double quit_time = (time18 - time17)/pre;
				if(quit_time>max_quit)
					max_quit = quit_time;
				if(quit_time<min_quit)
					min_quit = quit_time;
				avg_quit = avg_quit+quit_time;
				Quit_comm= json_qm.getBytes(StandardCharsets.UTF_8).length;
				Quit_store = 8;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("init:"+min_init+","+avg_init/max_repeat+","+max_init);
		System.out.println("deposit:"+min_dep+","+avg_dep/max_repeat+","+max_dep);
		System.out.println("record:"+min_rec+","+avg_rec/max_repeat+","+max_rec);
		System.out.println("evaluate:"+min_eval+","+avg_eval/max_repeat+","+max_eval);
		System.out.println("audit:"+min_aud+","+avg_aud/max_repeat+","+max_aud);
		System.out.println("authorize:"+min_auth+","+avg_auth/max_repeat+","+max_auth);
		System.out.println("inspect:"+min_insp+","+avg_insp/max_repeat+","+max_insp);
		System.out.println("confirm:"+min_cfm+","+avg_cfm/max_repeat+","+max_cfm);
		System.out.println("quit:"+min_quit+","+avg_quit/max_repeat+","+max_quit);

		System.out.println("deposit:"+Deposit_comm+","+Deposit_store);
		System.out.println("record:"+Record_comm+","+Record_store);
		System.out.println("evaluate:"+Evalute_comm+","+Evalute_store);
		System.out.println("audit:"+Audit_comm+","+Audit_store);
		System.out.println("authorize:"+Authorize_comm+","+Authorize_store);
		System.out.println("inspect:"+Inspect_comm+","+Inspect_store);
		System.out.println("confirm:"+Confirm_comm+","+Confirm_store);
		System.out.println("quit:"+Quit_comm+","+Quit_store);
	}
}
