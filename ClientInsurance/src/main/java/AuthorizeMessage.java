import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.miracl.core.BN254.ECP;

import java.math.BigInteger;

@DataType()
public class AuthorizeMessage {

    @Property()
    private int num;

    @Property()
    private byte[] ct_1;

    @Property()
    private byte[] ct_2;

    @Property()
    private ElgamalProofParameters proofs;

    public ElgamalProofParameters getProofs() {
        return proofs;
    }

    public int getNum() {
        return num;
    }

    public byte[] getCt_2() {
        return ct_2;
    }

    public byte[] getCt_1() {
        return ct_1;
    }

    public AuthorizeMessage(@JsonProperty("num") int num,
                            @JsonProperty("ct_1") byte[] ct_1,
                            @JsonProperty("ct_2") byte[] ct_2,
                            @JsonProperty("proofs") ElgamalProofParameters proofs)
    {
        this.num = num;
        this.ct_1 = ct_1;
        this.ct_2 = ct_2;
        this.proofs = proofs;
    }
}