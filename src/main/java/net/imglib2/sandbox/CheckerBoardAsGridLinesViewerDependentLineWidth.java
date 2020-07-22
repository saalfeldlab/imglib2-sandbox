package net.imglib2.sandbox;

import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converters;
import net.imglib2.position.FunctionRealRandomAccessible;
import net.imglib2.position.Functions;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Translation3D;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;

class CheckerBoardAsGridLinesViewerDependentLineWidth {

	public static void main(final String[] s) throws InterruptedException {

		final FunctionRealRandomAccessible<IntType> checker = Functions.realChecker(3, new IntType(0), new IntType(1));

		final Translation3D offset = new Translation3D(0.01, 0.01, 0.01);

		final RealRandomAccessible<IntType> grid = Converters.convert(
				RealViews.transformReal(checker, offset),
				RealViews.transformReal(checker, offset.inverse()),
				(a, b, c) -> {
					c.set(a.get() == b.get() ? 0 : 255);
				},
				new IntType());

		final BdvStackSource<IntType> bdv = BdvFunctions.show(
				grid,
				Intervals.createMinMax(-1, -1, -1, 1, 1, 1),
				"Grid");

		bdv.setDisplayRange(0, 255);

		bdv.getBdvHandle().getViewerPanel().addRenderTransformListener(
				(t) -> {
					final double x = t.get(0, 0);
					final double y = t.get(1, 0);
					final double z = t.get(2, 0);
					final double l = 0.5 / Math.sqrt(x * x + y * y + z * z);

					offset.set( l, l, l );
				});
	}
}