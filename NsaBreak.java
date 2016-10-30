
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.LinkedList;
import java.util.List;

class NsaBreak {

    public static void main(String[] args) {
        (new NsaBreak()).bruteforceKeyShouldBeFoundPlaintext();
    }

    private void bruteforceKeyShouldBeFoundPlaintext() {

        System.out.println(" --- Welcome to nsaBreakScript :) --- ");
        String plaintext = "";
        BigInteger cypher = new BigInteger("434384569820012709749978085023147407174684824178941182826833799495931410023794845922767533429746537016995520506439457550763575993604402054742042654701475990703513534158579743446096171193503041071008550601683001839024513922875537448251544812606790879783442587227277601837740236112564627038091170167413493638174350319534049389495771259593223970367601200671312435588244337256711215093040169407379877379400242759675821842258110296156050808143302683071999772732555638568114446076107646435540182476596386155629693774180289540430199704239923354089531930534807431109632462125230336414045829798557815327441670222304200");

        System.out.println(" --- finding min/max key!! --- ");
        BigInteger minKey = new BigInteger("9063554100000000000"); //findKey("ffffffffffffffffffffffffffffffff", cypher);
        BigInteger maxKey = findKey("00000000000000000000000000000000", cypher);
        System.out.println(String.format("Key[Min] ~ %d", minKey));
        System.out.println(String.format("Key[Max] ~ %d", maxKey));

        System.out.println(" --- finding rangeKey!! --- ");
        BigInteger rangeKey = getKeyRange(cypher);
        BigInteger increseKey = BigInteger.valueOf(1);
        System.out.println(String.format("RangeKey = %d", rangeKey));

        System.out.println(" --- START brute force!! --- ");
        int round = 0;
        while (minKey.compareTo(maxKey) == -1) {
            BigInteger cypherTemp = cypher;
            if (minKey.mod(rangeKey).intValue() == 0) {
                increseKey = rangeKey;
                try {
                    for (int i = 1; i <= 32; i++) {
                        cypherTemp = cypherTemp.divide(minKey);
                        plaintext = nsaBreakCharacter(minKey, cypherTemp) + plaintext;
                    }
                    break;
                } catch (InvalidKeyException ignore) {
                }
            }
            minKey = minKey.add(increseKey);
            if(round % 100000 == 0){
                System.out.println(String.format("find key [%d - %d] ", minKey, maxKey));
            }
            round++;
        }
        System.out.println("....");

        System.out.println("Cracked!!");
        System.out.println("minKey = " + minKey);
        System.out.println("plaintext = " + plaintext);

    }

    private String nsaBreakCharacter(BigInteger key, BigInteger cypher) throws InvalidKeyException {
        String hex = "0123456789abcdefABCDEF";
        for (int character = 0; character < hex.length(); character++) {
            char word = hex.charAt(character);
            if (cypher.subtract(BigInteger.valueOf((int) word)).mod(key).intValue() == 0) {
                return String.valueOf(word);
            }
        }
        throw new InvalidKeyException();
    }

    private BigInteger getKey(List<Integer> keyList) {
        BigInteger key = new BigInteger("0");
        for (int i = 0; i < keyList.size(); i++) {
            BigInteger positionValue = BigInteger.valueOf(keyList.get(i)).multiply(BigDecimal.valueOf(Math.pow(10, i)).toBigInteger());
            key = key.add(positionValue);
        }
        return key;
    }

    private int getPositionKey(BigInteger cypherTemp, List<Integer> keyList, int positionKey) {
        if(cypherTemp.compareTo(BigInteger.valueOf(0)) == 1){
            if(keyList.get(positionKey) == 9){
                keyList.add(++positionKey, 9);
            }else{
                keyList.set(positionKey, keyList.get(positionKey) + 1);
                keyList.set(--positionKey, keyList.get(positionKey) - 1);
            }
        }else if(cypherTemp.compareTo(BigInteger.valueOf(0)) == -1){
            if(keyList.get(positionKey) == 0){
                positionKey--;
            }
            keyList.set(positionKey, keyList.get(positionKey) - 1);
        }
        return positionKey;
    }

    private BigInteger findKey(String plaintext, BigInteger cypher) {
        BigInteger key;
        BigInteger cypherTemp;
        int positionKey = 0;
        List<Integer> keyList = new LinkedList<Integer>(){{
            add(0, 9);
        }};

        do {
            key = getKey(keyList);
            cypherTemp = cypher;
            for (int character = 0; character < plaintext.length(); character++) {
                BigInteger ascii = BigInteger.valueOf((int) plaintext.charAt(character));
                cypherTemp = cypherTemp.divide(key).subtract(ascii);
            }
            positionKey = getPositionKey(cypherTemp, keyList, positionKey);
        } while (cypherTemp.compareTo(BigInteger.valueOf(0)) != 0);

        return key.add(BigInteger.valueOf(1));
    }


    private BigInteger getKeyRange(BigInteger cypher) {
        BigInteger range = BigInteger.valueOf(1);
        BigInteger multinumber = BigInteger.valueOf(2);
        while (multinumber.intValue() <= 200000 && cypher.compareTo(BigInteger.valueOf(1)) == 1) {
            if(cypher.mod(multinumber).intValue() == 0){
                cypher = cypher.divide(multinumber);
                range = multinumber;
                multinumber = BigInteger.valueOf(2);
            }else {
                multinumber = multinumber.add(BigInteger.valueOf(1));
            }
        }

        return range;
    }

    /**
     * Default Function
     */
    private String nsaEncrypt(BigInteger key, String plainText) {
        BigInteger result = new BigInteger("0");
        for (int character = 0; character < plainText.length(); character++) {
            result = result.add(BigInteger.valueOf((int) plainText.charAt(character)));
            result = result.multiply(key);
        }
        return result.toString();
    }


}
