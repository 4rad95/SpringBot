package org.binance.springbot.analytic;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

public class PivotCalculator {

    public static class PivotPoints {
        public double PP; // Пивотная точка
        public double R1, R2, R3; // Сопротивления
        public double S1, S2, S3; // Поддержки

        public PivotPoints(double PP, double R1, double R2, double R3, double S1, double S2, double S3) {
            this.PP = PP;
            this.R1 = R1;
            this.R2 = R2;
            this.R3 = R3;
            this.S1 = S1;
            this.S2 = S2;
            this.S3 = S3;
        }

        @Override
        public String toString() {
            return String.format("PP: %.4f, R1: %.4f, R2: %.4f, R3: %.4f, S1: %.4f, S2: %.4f, S3: %.4f",
                    PP, R1, R2, R3, S1, S2, S3);
        }
    }

    public static PivotPoints calculatePivotPoints(BarSeries series) {

        Bar bar = series.getBar(series.getEndIndex());
        double high = bar.getHighPrice().doubleValue();
        double low = bar.getLowPrice().doubleValue();
        double close = bar.getClosePrice().doubleValue();

        // Рассчитываем пивотную точку
        double PP = (high + low + close) / 3.0;

        // Уровни сопротивления
        double R1 = 2 * PP - low;
        double R2 = PP + (high - low);
        double R3 = high + 2 * (PP - low);

        // Уровни поддержки
        double S1 = 2 * PP - high;
        double S2 = PP - (high - low);
        double S3 = low - 2 * (high - PP);

        return new PivotPoints(PP, R1, R2, R3, S1, S2, S3);
    }
}