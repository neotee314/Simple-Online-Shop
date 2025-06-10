package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.ZipCodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;


import jakarta.persistence.*;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class ZipCode implements ZipCodeType {

    private String zipCode;

    private ZipCode(String zipCode)  {

        this.zipCode = zipCode;
    }
    public static ZipCodeType of(String zipCode) {
        if (zipCode == null || !isValidZipCode(zipCode)) {
            throw new ShopException("Invalid zip code");
        }
        return new ZipCode(zipCode);
    }

    private static boolean isValidZipCode(String zipCode) {
        if (zipCode.length() != 5) {
            return false;
        }
        if (!zipCode.matches("\\d{5}")) {
            return false;
        }
        return !zipCode.endsWith("0000");
    }

    @Override
    public String toString() {
        return zipCode;
    }

    @Override
    public int difference(ZipCodeType otherZipCode) throws ShopException {
        if (otherZipCode == null) throw new ShopException("Invalid zip code");
        String thisZip = this.toString();
        String otherZip = otherZipCode.toString();

        if (thisZip.length() != otherZip.length()) {
            throw new ShopException("Zip codes must be of same length");
        }

        int firstDiffPos = -1;
        for (int i = 0; i < thisZip.length(); i++) {
            if (thisZip.charAt(i) != otherZip.charAt(i)) {
                firstDiffPos = i;
                break;
            }
        }

        if (firstDiffPos == -1) {
            return 0;
        }

        // Base difference based on position (more left = more significant)
        int positionDifference = (thisZip.length() - firstDiffPos) * 1000;

        if (firstDiffPos == 0) {
            int thisDigit = Character.getNumericValue(thisZip.charAt(0));
            int otherDigit = Character.getNumericValue(otherZip.charAt(0));

            // Calculate circular distance (0 and 9 are adjacent)
            int digitDiff = Math.abs(thisDigit - otherDigit);
            digitDiff = Math.min(digitDiff, 10 - digitDiff);

            // Add distance to base (closer digits = smaller total difference)
            return positionDifference + digitDiff;
        }

        return positionDifference;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ZipCode other = (ZipCode) obj;
        return zipCode.equals(other.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode);
    }
}
