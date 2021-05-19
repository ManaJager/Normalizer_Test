package normalizer;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class test {
    static final int SAMPLES = 10000000;
    static final double[] scoreScale = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};

    static final double rawRiskScore = 0.79;

    public static void main(String[] args) {
        //Массив для сэмплов rawRickScore
        double[] xVals = new double[SAMPLES];

        //Наполнение массива сэмплов rawRickScore
        for (int i = 0; i < SAMPLES; i++) {
            xVals[i] = Math.random();
            //System.out.println(xVals[i]);
        }

        int riskScore = interpolate(percentilesCalc(xVals), rawRiskScore);
        System.out.println("Normalized riskScore for " + SAMPLES + " random rawRiskScore samples = " + riskScore);
    }

    //Функция расчета перцентилей по сэмплам
    public static double[] percentilesCalc(double[] rawScores) {
        double[] percentiles = new double[11];
        Percentile p = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
        p.setData(rawScores);
        percentiles[0] = p.evaluate(0.00000000001); //на 0 вылетает ошибка, так что можно бесконечно малое значение вместо этого брать
        percentiles[1] = p.evaluate(50);
        percentiles[2] = p.evaluate(70);
        percentiles[3] = p.evaluate(80);
        percentiles[4] = p.evaluate(90);
        percentiles[5] = p.evaluate(95);
        percentiles[6] = p.evaluate(97);
        percentiles[7] = p.evaluate(99);
        percentiles[8] = p.evaluate(99.5);
        percentiles[9] = p.evaluate(99.75);
        percentiles[10] = p.evaluate(100);

        return percentiles;
    }

    //Функция интерполяции по перцентилям
    public static int interpolate(double[] percentiles, double rawRiskScore) {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        PolynomialSplineFunction function = linearInterpolator.interpolate(percentiles, scoreScale);

        double riskScore;
        try {
            riskScore = function.value(rawRiskScore);
        } catch (Exception e) {
            riskScore = 0;
        }
        return (int)riskScore;
    }
}
