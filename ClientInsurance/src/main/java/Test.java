import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.SerializationUtils;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.miracl.core.BN254.BIG;
import org.miracl.core.BN254.ECP;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(Math.pow(10,6));
        /***
        BigInteger[] x = new BigInteger[Parameters.nlength-1];
        for(int i=0;i<Parameters.nlength-1;i++) {
            x[i] = Utils.randomRange(Parameters.range_x_1, Parameters.range_x_2);
        }
        BigInteger random = Utils.randomRange(Parameters.range_r_1, Parameters.range_r_2);
        BigInteger v = Utils.randomZbits(Utils.tau+Utils.Nlength);
        BigInteger c = Parameters.g.modPow(random, Parameters.N).multiply(Parameters.h.modPow(v, Parameters.N)).mod(Parameters.N);

        BigInteger a = Utils.randomRange(Parameters.range_a_1, Parameters.range_a_2);
        BigInteger b = Utils.randomRange(Parameters.range_b_1, Parameters.range_b_2);

        BigInteger r_a = random.multiply(a);
        BigInteger v_a = v.multiply(a);

        BigInteger E_x = BigInteger.ONE;
        for (int i = 0; i < Parameters.nlength-1; i++) {
            E_x = E_x.multiply(Parameters.W[i].modPow(x[i],Parameters.Nsquare));
        }

        E_x = E_x.multiply(Parameters.W[Parameters.nlength-1])
                .multiply(Parameters.gg.modPow(random,Parameters.Nsquare)).mod(Parameters.Nsquare);

        BigInteger E_x_a = E_x.modPow(a,Parameters.Nsquare)
                .multiply(Parameters.gg.modPow(r_a.negate(),Parameters.Nsquare))
                .multiply(Parameters.gg.modPow(b,Parameters.Nsquare)).mod(Parameters.Nsquare);
        Paillier cipher = new Paillier(Utils.Nlength,Parameters.p,Parameters.q,Parameters.lambda,Parameters.N,Parameters.Nsquare,Parameters.gg,Parameters.mu);
        PaillierProof pproof = new PaillierProof();
        ReportProofParameters rpp = pproof.prove_driving_record(Parameters.g, Parameters.h, cipher, c,
                random, v, a, b, r_a, v_a, E_x, E_x_a, x, Parameters.W,
                Parameters.range_a_1, Parameters.range_a_2, Parameters.range_b_1, Parameters.range_b_2, Parameters.range_r_1,
                Parameters.range_r_2, Parameters.range_x_1, Parameters.range_x_2);
        BIG m = Utils.BigInteger_to_BIG(new BigInteger("123456"));
        AES aes = new AES(Utils.getKey(Parameters.g_.mul(m)),Utils.getIV());
        byte[] text = Utils.fillBlock(Base64.getEncoder().encodeToString(SerializationUtils.serialize(x))).getBytes(StandardCharsets.UTF_8);
        byte[] ctt = aes.CBC_encrypt(text);

        ReportMessage rm = new ReportMessage(E_x_a,E_x,c,rpp,ctt);
        Asset asset = new Asset(3000);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json_rm = mapper.writeValueAsString(rm);
        ReportMessage rm_new = mapper.readValue(json_rm,ReportMessage.class);
        String json_asset = mapper.writeValueAsString(asset);
        Asset asset_new = mapper.readValue(json_asset,Asset.class);
        System.out.println(asset_new.getDeposit());

        Paillier cipher_new = new Paillier(Utils.Nlength,Parameters.p,Parameters.q,
                Parameters.lambda,Parameters.N,Parameters.Nsquare,Parameters.gg,Parameters.mu);
        PaillierProof pproof_new = new PaillierProof();

        System.out.println(pproof_new.verify_driving_record(Parameters.g, Parameters.h, cipher_new, rm_new.getC(), rm_new.getE_x_a(), rm_new.getE_x(), Parameters.W, rm_new.getRpp(),
                Parameters.range_a_1, Parameters.range_a_2, Parameters.range_b_1, Parameters.range_b_2,
                Parameters.range_r_1, Parameters.range_r_2, Parameters.range_x_1, Parameters.range_x_2));

        BigInteger alpha = Utils.randomRange(Parameters.range_alpha_1,Parameters.range_alpha_2);
        BigInteger beta = Utils.randomRange(Parameters.range_beta_1,Parameters.range_beta_2);

        BigInteger E_cipher = E_x_a.modPow(alpha,Parameters.Nsquare)
                .multiply(Parameters.gg.modPow(beta,Parameters.Nsquare)).mod(Parameters.Nsquare);
        BigInteger mm = cipher.decrypt(E_cipher);
        BigInteger Gamma = Utils.randomZStarN(Utils.Nlength,Parameters.N);
        BigInteger U = Parameters.gg.modPow(mm,Parameters.Nsquare).multiply(Gamma.modPow(Parameters.N,Parameters.Nsquare)).mod(Parameters.Nsquare);
        BigInteger D = E_cipher.multiply(U.modPow(BigInteger.ONE.negate(),Parameters.Nsquare)).mod(Parameters.Nsquare);
        BigInteger phi_N = cipher.getP().subtract(BigInteger.ONE).multiply(cipher.getQ().subtract(BigInteger.ONE));
        BigInteger exp_tmp = Parameters.N.modPow(BigInteger.ONE.negate(), phi_N);
        BigInteger Z = D.modPow(exp_tmp,Parameters.N);

        RecordProofParameters repp = pproof.prove_driving_safety(
                Parameters.g,Parameters.h,cipher,Z,E_cipher,E_x_a,U,Gamma,alpha,beta,
                Parameters.range_alpha_1, Parameters.range_alpha_2,Parameters.range_beta_1,Parameters.range_beta_2);

        ConfirmMessage cm = new ConfirmMessage(U, E_cipher,mm, repp);

        String json_cm = mapper.writeValueAsString(cm);
        ConfirmMessage cm_new = mapper.readValue(json_cm,ConfirmMessage.class);
        System.out.println(pproof.verify_driving_safety(
                Parameters.g,Parameters.h,cipher,cm_new.getE_cipher(),rm_new.getE_x_a(),cm_new.getU(),cm_new.getMm(),cm_new.getRepp(),Parameters.range_alpha_1,
                Parameters.range_alpha_2,Parameters.range_beta_1,Parameters.range_beta_2));

        Elgamal elgamal = new Elgamal();
        elgamal.setParams(Parameters.order,Parameters.x,Parameters.g_,Parameters.h_,Parameters.bits);
        BIG r = BIG.randomnum(Parameters.order, elgamal.getRng());
        ECP[] ct = elgamal.encrypt(m, r);
        ElgamalProofParameters epp = elgamal.prove(m,r,ct);

        byte[] ct_1 = new byte[33];
        byte[] ct_2 = new byte[33];
        ct[0].toBytes(ct_1,true);
        ct[1].toBytes(ct_2,true);

        AuthorizeMessage authmsg = new AuthorizeMessage(1,ct_1,ct_2, epp);
        String json_authm = mapper.writeValueAsString(authmsg);
        AuthorizeMessage authmsg_new = mapper.readValue(json_authm,AuthorizeMessage.class);
        ECP[] ct_new = new ECP[2];
        ct_new[0] = ECP.fromBytes(authmsg_new.getCt_1());
        ct_new[1] = ECP.fromBytes(authmsg_new.getCt_2());
        ElgamalProofParameters epp_new = authmsg_new.getProofs();
        System.out.println(elgamal.verify(ct_new,epp_new));
         ***/
    }
}
