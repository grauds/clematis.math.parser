// Created: 05.12.2006 T 16:02:35
package org.clematis.math;

/**
 * Load reference algorithms
 */
public class ReferenceAlgorithmLoader
{
    /**
     * Reference
     */
    private org.clematis.math.reference.algorithm.Algorithm algorithm = null;

    private org.clematis.math.reference.algorithm.AlgorithmFactory loadReference()
    {
        try
        {
            Class<org.clematis.math.reference.algorithm.AlgorithmFactory> clazz =
                    (Class<org.clematis.math.reference.algorithm.AlgorithmFactory>)
                    Class.forName("org.clematis.math.algorithm.AlgorithmFactory");
            return clazz.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public org.clematis.math.reference.algorithm.Algorithm getAlgorithmReference(String sAlg, String initialConditions)
            throws Exception
    {
        if ( algorithm == null )
        {
            algorithm = (org.clematis.math.reference.algorithm.Algorithm)
                org.clematis.math.reference.algorithm.AlgorithmFactory.loadAlgorithm(sAlg, initialConditions);
        }
        return algorithm;
    }

    public org.clematis.math.reference.OutputFormatSettings getOutputFormatSettings()
    {
        return new org.clematis.math.reference.OutputFormatSettings();
    }
}
