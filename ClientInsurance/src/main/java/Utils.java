import org.apache.commons.lang3.SerializationUtils;
import org.miracl.core.BN254.*;
import org.miracl.core.HMAC;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class Utils {

    public final static int Nlength = 2048;
    public final static int tau = 256;

    public static String object_serialize(Serializable obj)
    {
        return Base64.getEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static Object object_deserialize(String data)
    {
        return SerializationUtils.deserialize(Base64.getDecoder().decode(data));
    }

    public static BigInteger Hash(byte[] input)
    {
        byte[] encodedhash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            encodedhash = digest.digest(input);
        }catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return new BigInteger(1, encodedhash);
    }

    // return a random integer in Z_n
    public static BigInteger randomZN(int modLength, BigInteger n)
    {
        BigInteger r;

        do
        {
            r = new BigInteger(modLength, new Random());
        }
        while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(n) >= 0);

        return r;
    }

    public static BigInteger randomZbits(int bits)
    {
        return new BigInteger(bits, new SecureRandom());
    }

    public static BigInteger randomRange(BigInteger min, BigInteger max)
    {
        SecureRandom sr = new SecureRandom();
        BigDecimal tmp_max = new BigDecimal(max);
        BigDecimal tmp_min = new BigDecimal(min);
        BigDecimal tmp = (tmp_max.subtract(tmp_min).multiply(new BigDecimal(sr.nextDouble()))).add(tmp_min);
        return tmp.toBigInteger();
    }

    // return a random integer in Z*_n
    public static BigInteger randomZStarN(int modLength, BigInteger n)
    {
        BigInteger r;

        do
        {
            r = new BigInteger(modLength, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }

    // return a random integer in Z*_{n^2}
    public static BigInteger randomZStarNSquare(int modLength, BigInteger nsquare)
    {
        BigInteger r;

        do
        {
            r = new BigInteger(modLength * 2, new Random());
        }
        while (r.compareTo(nsquare) >= 0 || r.gcd(nsquare).intValue() != 1);

        return r;
    }

    public static BigInteger[] two_squares(BigInteger p)
    {
        try {
            BigInteger a = find_root(p);
            return gaussian_gcd(p,BigInteger.ZERO, a, BigInteger.ONE);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static BigInteger mod_special(BigInteger a, BigInteger n) throws Exception
    {
        if(n.signum()==-1||n.signum()==0)
            throw new Exception("The modulus is negative or zero");
        a = a.mod(n);
        if(a.pow(2).compareTo(n)==1)
            a = a.subtract(n);
        return a;
    }

    public static BigInteger powmod_special(BigInteger a, BigInteger r, BigInteger n) throws Exception
    {
        BigInteger out = BigInteger.ONE;
        while(r.signum()==1)
        {
            if(r.mod(BigInteger.TWO).intValue()==1)
            {
                r = r.subtract(BigInteger.ONE);
                out = mod_special(out.multiply(a), n);
            }
            r = r.divide(BigInteger.TWO);
            a = mod_special(a.pow(2), n);
        }
        return out;
    }

    public static BigInteger quo_special(BigInteger a, BigInteger n) throws Exception
    {
        if(n.signum()==-1||n.signum()==0)
            throw new Exception("The modulus is negative or zero");
        BigInteger out = a.subtract(mod_special(a,n));
        out = out.divide(n);
        return out;
    }

    public static BigInteger[] grem(BigInteger w_0, BigInteger w_1, BigInteger z_0, BigInteger z_1) throws Exception
    {
        BigInteger n = z_0.pow(2).add(z_1.pow(2));
        if(n.signum()==0)
            throw new Exception("division by zero");
        BigInteger u0 = quo_special(w_0.multiply(z_0).add(w_1.multiply(z_1)),n);
        BigInteger u1 = quo_special(w_1.multiply(z_0).subtract(w_0.multiply(z_1)),n);
        BigInteger v0 = w_0.subtract(z_0.multiply(u0)).add(z_1.multiply(u1));
        BigInteger v1 = w_1.subtract(z_0.multiply(u1)).subtract(z_1.multiply(u0));
        return new BigInteger[]{v0,v1};
    }

    public static BigInteger[] gaussian_gcd(BigInteger w_0, BigInteger w_1, BigInteger z_0, BigInteger z_1) throws Exception
    {
        while(z_0.signum()!=0 || z_1.signum()!=0)
        {
            BigInteger[] new_z = grem(w_0,w_1,z_0,z_1);
            w_0 = z_0;
            w_1 = z_1;
            z_0 = new_z[0];
            z_1 = new_z[1];
        }
        return new BigInteger[]{w_0,w_1};
    }

    public static boolean is_perfect_square(BigInteger a)
    {
        BigInteger b = a.sqrt();
        if(b.pow(2).compareTo(a) == 0)
            return true;
        return false;
    }

    public static BigInteger find_root(BigInteger p) throws Exception
    {
        if(p.intValue()==1)
            throw new Exception("The prime is two small");
        if(p.mod(BigInteger.valueOf(4)).intValue()!=1)
            throw new Exception("The prime is not congruent to 1");
        BigInteger k = p.divide(BigInteger.valueOf(4));
        BigInteger j = BigInteger.TWO;
        BigInteger a,b;
        while (true)
        {
            a = powmod_special(j, k, p);
            b = mod_special(a.pow(2),p);
            if(b.intValue()==-1)
                return a;
            if(b.intValue()!=1)
                throw new Exception("The number is not a prime");
            j = j.add(BigInteger.ONE);
        }
    }

    public static BigInteger BIG_to_BigInteger(BIG input)
    {
        String str_input = input.toString();
        return new BigInteger(str_input, 16);
    }

    public static BIG BigInteger_to_BIG(BigInteger input)
    {
        // System.out.println(input.toString(16));
        if(input.bitLength()> CONFIG_BIG.BASEBITS*BIG.NLEN)
        {
            return null;
        }
        int num = CONFIG_BIG.BASEBITS/4;
        long[] longArray = new long[BIG.NLEN];
        String str = input.toString(16);
        int start = str.length();
        int end = str.length();
        for(int i = 0;i < BIG.NLEN;i++)
        {
            // System.out.println("start - num,end:"+(start - num)+","+ end);
            if(start -  num < 0)
            {
                start = num;
            }

            longArray[i] = Long.valueOf(str.substring(start - num,end),16);
            if(start == num)
                break;
            start = start - num;
            end = end - num;
        }
        return new BIG(longArray);
    }

    public static BigInteger[] three_squares(BigInteger n) throws Exception
    {
        BigInteger FOUR = BigInteger.valueOf(4);
        if(n.mod(FOUR).signum()==0)
        {
            BigInteger[] values = three_squares(n.divide(FOUR));
            values[0] = values[0].multiply(BigInteger.TWO);
            values[1] = values[1].multiply(BigInteger.TWO);
            values[2] = values[2].multiply(BigInteger.TWO);
            return values;
        }
        else if(n.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(7))==0)
        {
            throw new Exception("No representation of three squares");
        }
        else if(n.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(3))==0)
        {
            BigInteger nsqrt = n.sqrt();
            BigInteger p,x;
            do{
                x = randomZbits(nsqrt.bitLength()-1);
                p = n.subtract(x.pow(2)).divide(BigInteger.TWO);
            }while (!p.isProbablePrime(64));
            BigInteger[] values = two_squares(p);
            return new BigInteger[]{x,values[0].add(values[1]),values[0].subtract(values[1])};
        }
        else if(is_perfect_square(n))
        {
            return new BigInteger[]{n.sqrt(),BigInteger.ZERO,BigInteger.ZERO};
        }
        else if(n.mod(FOUR).intValue()==1 || n.mod(FOUR).intValue()==2)
        {
            BigInteger nsqrt = n.sqrt();
            BigInteger p,x;
            do{
                x = randomZbits(nsqrt.bitLength()-1);
                p = n.subtract(x.pow(2));
            }while (!p.isProbablePrime(64));
            BigInteger[] values = two_squares(p);
            return new BigInteger[]{x,values[0],values[1]};
        }
        return null;
    }

    public static String bytes_to_string(byte[] input)
    {
        return Base64.getEncoder().encodeToString(input);
    }

    public static byte[] string_to_bytes(String input)
    {
        return Base64.getDecoder().decode(input);
    }

    static int ceil(int a,int b) {
        return (((a)-1)/(b)+1);
    }

    private static FP[] hash_to_field(int hash,int hlen,byte[] DST,byte[] M,int ctr) {
        BIG q = new BIG(ROM.Modulus);
        int L = ceil(q.nbits()+CONFIG_CURVE.AESKEY*8,8);
        FP [] u = new FP[ctr];
        byte[] fd=new byte[L];

        byte[] OKM=HMAC.XMD_Expand(hash,hlen,L*ctr,DST,M);
        for (int i=0;i<ctr;i++)
        {
            for (int j=0;j<L;j++)
                fd[j]=OKM[i*L+j];
            u[i]=new FP(DBIG.fromBytes(fd).mod(q));
        }

        return u;
    }

    public static ECP hash_to_point(byte[] M) {
        String dst= new String("BLS_SIG_BN254G1_XMD:SHA-256_SVDW_RO_NUL_");
        FP[] u=hash_to_field(HMAC.MC_SHA2, CONFIG_CURVE.HASH_TYPE,dst.getBytes(),M,2);

        ECP P=ECP.map2point(u[0]);
        ECP P1=ECP.map2point(u[1]);
        P.add(P1);
        P.cfp();
        P.affine();
        return P;
    }

    public static byte[] truncate(byte[] array, int newLength) {
        if (array.length < newLength) {
            return array;
        } else {
            byte[] truncated = new byte[newLength];
            System.arraycopy(array, 0, truncated, 0, newLength);

            return truncated;
        }
    }

    public static String fillBlock(String text) {
        int spaceNum = text.getBytes().length%16==0?0:16-text.getBytes().length%16;
        for (int i = 0; i<spaceNum; i++) text += " ";
        return text;
    }

    public static byte[] getKey(ECP m) {
        byte[] key_bytes = new byte[100];
        m.toBytes(key_bytes,true);
        key_bytes = Utils.truncate(key_bytes,16);
        return key_bytes;
    }

    public static byte[] getIV()
    {
        byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        return iv;
    }
}
