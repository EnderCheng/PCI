import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.math.BigInteger;

@DataType()
public class ConfirmMessage {

    @Property()
    private final BigInteger U;

    @Property()
    private final BigInteger E_cipher;

    @Property()
    private final BigInteger mm;

    @Property()
    private final RecordProofParameters repp;

    public BigInteger getE_cipher() {
        return E_cipher;
    }

    public BigInteger getMm() {
        return mm;
    }

    public BigInteger getU() {
        return U;
    }

    public RecordProofParameters getRepp() {
        return repp;
    }

    public ConfirmMessage(@JsonProperty("U") BigInteger U,
                          @JsonProperty("E_cipher") BigInteger E_cipher,
                          @JsonProperty("mm") BigInteger mm,
                          @JsonProperty("repp") RecordProofParameters repp)
    {
        this.U = U;
        this.E_cipher = E_cipher;
        this.mm = mm;
        this.repp = repp;
    }

}
