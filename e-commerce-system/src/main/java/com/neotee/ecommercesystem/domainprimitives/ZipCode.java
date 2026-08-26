package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.ZipCodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class ZipCode implements ZipCodeType {

    private String zipCode;

    private ZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public static ZipCodeType of(String zipCode) {
        if (zipCode == null || !isValidZipCode(zipCode)) {
            throw new DomainValidationException("zipCode", "Invalid zip code");
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
    public int difference(ZipCodeType otherZipCode) throws DomainValidationException {
        if (otherZipCode == null) {
            throw new DomainValidationException(
                    "otherZipCode",
                    "Invalid zip code"
            );
        }

        String thisZip = this.toString();
        String otherZip = otherZipCode.toString();

        if (thisZip.length() != otherZip.length()) {
            throw new DomainValidationException(
                    "zipCode",
                    "Zip codes must be of same length"
            );
        }

        int firstDiffPos = -1;

        for (int i = 0; i < thisZip.length(); i++) {
            if (thisZip.charAt(i) != otherZip.charAt(i)) {
                firstDiffPos = i;
                break;
            }
        }

        // Same ZIP code
        if (firstDiffPos == -1) {
            return 0;
        }

        // Difference grows according to how significant
        // the first differing digit is.
        int baseDifference =
                (thisZip.length() - firstDiffPos) * 1000;

        // Special handling for the first digit:
        // 0 and 9 are considered adjacent.
        if (firstDiffPos == 0) {
            int thisDigit = Character.digit(thisZip.charAt(0), 10);
            int otherDigit = Character.digit(otherZip.charAt(0), 10);

            int digitDifference = Math.abs(thisDigit - otherDigit);
            digitDifference = Math.min(
                    digitDifference,
                    10 - digitDifference
            );

            return baseDifference + digitDifference;
        }

        return baseDifference;
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