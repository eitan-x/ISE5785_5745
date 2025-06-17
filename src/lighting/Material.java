package lighting;

import primitives.Double3;

/**
 * Material class represents the material of a geometry
 */
public class Material {

    /**
     * kD is the diffuse factor
     */
    public Double3 kD = Double3.ZERO;

    /**
     * kA is the diffuse factor
     */
    public Double3 kA = Double3.ONE;

    /**
     * kS is the specular factor
     */
    public Double3 kS = Double3.ZERO;
    /**
     * kT is the transparency factor
     */

    public int nShininess = 0;

    /**
     * kR is the reflection factor
     */
    public Double3 kR = Double3.ZERO;

    /**
     * kT is the transparency factor
     */
    public Double3 kT = Double3.ZERO;

    /**
     * setter for transparency factor
     *
     * @param kt the transparency factor
     * @return the material
     */
    public Material setKt(Double3 kt) {
        this.kT = kt;
        return this;
    }

    /**
     * setter for transparency factor
     *
     * @param kt the transparency factor
     * @return the material
     */
    public Material setKt(double kt) {
        this.kT = new Double3(kt);
        return this;
    }


    /**
     * setter for reflection factor
     *
     * @param kr the reflection factor
     * @return the material
     */
    public Material setKr(Double3 kr) {
        this.kR = kr;
        return this;
    }

    /**
     * setter for reflection factor
     *
     * @param kr the reflection factor
     * @return the material
     */
    public Material setKr(double kr) {
        this.kR = new Double3(kr);
        return this;
    }

    /**
     * Material setter
     *
     * @param kA the diffuse factor
     * @return the material
     */
    public Material setkA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Material setter
     *
     * @param kA the diffuse factor
     * @return the material
     */
    public Material setkA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Material setter
     *
     * @param kD the diffuse factor
     * @return the material
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Material setter
     *
     * @param kD the diffuse factor
     * @return the material
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Material setter
     *
     * @param kS the specular factor
     * @return the material
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Material setter
     *
     * @param kS the specular factor
     * @return the material
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Material setter
     *
     * @param nShininess the shininess factor
     * @return the material
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}