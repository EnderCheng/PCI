package ca.uwaterloo.cheng;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.miracl.core.BN254.ECP;


@Contract(
        name = "InsuranceContract",
        info = @Info(
                title = "InsuranceContract",
                description = "Deployed Chaincode in Hyperledger Fabric v2.1",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "c225huan@uwaterloo.ca",
                        name = "Cheng Huang",
                        url = "https://hyperledger.example.com")))

@Default
public class InsuranceContract implements ContractInterface {

    final ObjectMapper mapper = new ObjectMapper();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {

    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String Deposit(final Context ctx, final String msg) {
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Asset asset = null;
        try {
            asset = mapper.readValue(msg, Asset.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        String deposit = Integer.toString(asset.getDeposit());
        stub.putStringState("deposit", deposit);
        stub.putStringState("deposit_state", "0");

        return deposit;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean Record(final Context ctx, final String msg) {
        boolean res = false;
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ReportMessage rm = null;
        try {
            rm = mapper.readValue(msg, ReportMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        Paillier cipher = new Paillier(Utils.Nlength,Parameters.p,Parameters.q,
                Parameters.lambda,Parameters.N,Parameters.Nsquare,Parameters.gg,Parameters.mu);
        PaillierProof pproof = new PaillierProof();
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState("report",msg);
        res =  pproof.verify_driving_record(Parameters.g, Parameters.h, cipher, rm.getC(), rm.getE_x_a(), rm.getE_x(), Parameters.W, rm.getRpp(),
                Parameters.range_a_1, Parameters.range_a_2, Parameters.range_b_1, Parameters.range_b_2,
                Parameters.range_r_1, Parameters.range_r_2, Parameters.range_x_1, Parameters.range_x_2);
        return res;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean Evaluate(final Context ctx, final String msg) {
        boolean res = false;
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ReportMessage rm = null;

        ConfirmMessage cm = null;
        try {
            rm = mapper.readValue(stub.getStringState("report"), ReportMessage.class);
            cm = mapper.readValue(msg,ConfirmMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        Paillier cipher = new Paillier(Utils.Nlength,Parameters.p,Parameters.q,
                Parameters.lambda,Parameters.N,Parameters.Nsquare,Parameters.gg,Parameters.mu);
        PaillierProof pproof = new PaillierProof();

        stub.putStringState("record",msg);
        res =  pproof.verify_driving_safety(
                Parameters.g,Parameters.h,cipher,cm.getE_cipher(),rm.getE_x_a(),cm.getU(),cm.getMm(),cm.getRepp(),Parameters.range_alpha_1,
                Parameters.range_alpha_2,Parameters.range_beta_1,Parameters.range_beta_2);
        return res;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String Audit(final Context ctx, final String msg) {
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        AuditMessage am = null;
        try {
            am = mapper.readValue(msg, AuditMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        String num = Integer.toString(am.getNum());
        stub.putStringState("audit_num",num);
        return num;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean Authorize(final Context ctx, final String msg) {
        boolean res = false;
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        AuthorizeMessage authm = null;
        try {
            authm = mapper.readValue(msg, AuthorizeMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        stub.putStringState("auth",msg);
        Elgamal elgamal = new Elgamal();
        elgamal.setParams(Parameters.order,Parameters.x,Parameters.g_,Parameters.h_,Parameters.bits);
        ECP[] ct_new = new ECP[2];
        ct_new[0] = ECP.fromBytes(authm.getCt_1());
        ct_new[1] = ECP.fromBytes(authm.getCt_2());
        ElgamalProofParameters epp_new = authm.getProofs();
        res = elgamal.verify(ct_new,epp_new);
        return res;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String Inspect(final Context ctx, final String msg) {
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        InspectMessage im = null;
        try {
            im = mapper.readValue(msg, InspectMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        if(im.getRes())
            stub.putStringState("inspect","true");
        if(!im.getRes()) {
            stub.putStringState("inspect", "false");
            TransferAsset(ctx,"1");
        }
        return "Inspect End";
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String state) {
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState("deposit_state", state);
        return state;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String Confirm(final Context ctx, final String msg) {
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        NoAuditMessage nam = null;
        try {
            nam = mapper.readValue(msg, NoAuditMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        stub.putStringState("No Audit", "Confirm");
        TransferAsset(ctx,"2");
        return nam.getChoice();
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String Quit(final Context ctx, final String msg) {
        ChaincodeStub stub = ctx.getStub();
        mapper.setPropertyNamingStrategy(new MyNamingStrategy());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        QuitMessage qm = null;
        try {
            qm = mapper.readValue(msg, QuitMessage.class);
        }catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        TransferAsset(ctx,"2");
        stub.putStringState("Quit", "True");
        return qm.getDecision();
    }






}
