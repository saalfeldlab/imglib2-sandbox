package net.imglib2.sandbox;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converters;
import net.imglib2.position.FunctionRealRandomAccessible;
import net.imglib2.position.Functions;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;

class AnimatedJuliaSetCheckers {

	public static void main(final String[] s) throws InterruptedException {

		final double r = 0.7885;
		final ComplexDoubleType c = new ComplexDoubleType(1, 0);

		final FunctionRealRandomAccessible<IntType> juliaSet = new FunctionRealRandomAccessible<>(
				2,
				(x, y) -> {
					int i = 0;
					double v = 0, ax = x.getDoublePosition(0), ay = x.getDoublePosition(1);
					while (i < 64 && v < 4096) {
						final double e = ax * ax - ay * ay;
						ay = 2 * ax * ay;
						ax = e + c.getRealDouble();
						ay += c.getImaginaryDouble();
						v = Math.sqrt(ax * ax + ay * ay);
						++i;
					}
					y.set(i);
				},
				IntType::new);

		final FunctionRealRandomAccessible<IntType> checkerBoard = Functions.realChecker(2, new IntType(0), new IntType(64));

		final RealRandomAccessible<IntType> mixed = Converters.convert(
				juliaSet,
				checkerBoard,
				(x, y, z) -> {
					z.set(y.get() == 0 ? x.get() : 64 - x.get());
				},
				new IntType());

		final BdvStackSource<?> bdv = BdvFunctions.show(
				mixed,
				Intervals.createMinMax(-1, -1, 1, 1),
				"Julia Set Checkers",
				BdvOptions.options().is2D());
		bdv.setDisplayRange(0, 64);

		for (double phi = 0;;phi += 0.005) {
			if (phi > Math.PI * 2) phi -= Math.PI * 2;
			c.setReal(r * Math.cos(phi));
			c.setImaginary(r * Math.sin(phi));

			bdv.getBdvHandle().getViewerPanel().requestRepaint();

			Thread.sleep(1000/30);
		}
	}
}