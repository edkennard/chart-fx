package de.gsi.financial.samples;

import static de.gsi.financial.samples.service.period.IntradayPeriod.IntradayPeriodEnum.M;

import java.util.HashMap;

import javafx.application.Application;

import de.gsi.chart.XYChart;
import de.gsi.chart.renderer.spi.financial.FootprintRenderer;
import de.gsi.chart.renderer.spi.financial.css.FinancialColorSchemeConstants;
import de.gsi.chart.renderer.spi.financial.service.footprint.FootprintRendererAttributes;
import de.gsi.dataset.spi.DefaultDataSet;
import de.gsi.dataset.spi.financial.OhlcvDataSet;
import de.gsi.financial.samples.service.addon.AbsorptionConsolidationAddon;
import de.gsi.financial.samples.service.consolidate.OhlcvConsolidationAddon;
import de.gsi.financial.samples.service.footprint.AbsorptionClusterRendererPaintAfterEP;
import de.gsi.financial.samples.service.footprint.DiagonalDominantNbColumnColorGroupService;
import de.gsi.financial.samples.service.footprint.FootprintRenderedAPIAdapter;
import de.gsi.financial.samples.service.period.IntradayPeriod;

/**
 * Tick FOOTPRINT realtime processing. Demonstration of re-sample data to 2M timeframe.
 * Support/Resistance range levels added.
 * YWatchValueIndicator for better visualization of y-values, auto-handling of close prices and manual settings of price levels.
 *
 * @author afischer
 */
public class FinancialRealtimeFootprintSample extends FinancialRealtimeCandlestickSample {
    @Override
    protected void configureApp() {
        title = "Replay FOOTPRINT Tick Data in real-time (press 'replay' button, zoom by mousewheel)";
        theme = FinancialColorSchemeConstants.DARK;
        resource = "REALTIME_OHLC_TICK";
        timeRange = "2016/07/29 13:25-2016/07/29 14:25";
        tt = "00:00-23:59"; // time template whole day session
        replayFrom = "2016/07/29 13:58";
        // price consolidation addons (extensions)
        consolidationAddons = new HashMap<>();
        consolidationAddons.put("footprintCalcAddons", new OhlcvConsolidationAddon[] {
                                                               new AbsorptionConsolidationAddon(false, 70, 3, 0.33d, 100.0d) });
        // parameter extendedCalculation ensures calculation of all necessary data for footprints features
        // parameter calculationAddonServicesType: possible add addon services for specific footprint additional features paintings
        period = new IntradayPeriod(M, 2.0, 0.0, true, "footprintCalcAddons");
    }

    protected void prepareRenderers(XYChart chart, OhlcvDataSet ohlcvDataSet, DefaultDataSet indiSet) {
        // configure footprint attributes (create defaults, and modify it by .setAttribute() methods
        FootprintRendererAttributes footprintAttrs = FootprintRendererAttributes.getDefaultValues(theme);

        // create and apply renderers
        FootprintRenderer renderer = new FootprintRenderer(
                new FootprintRenderedAPIAdapter(footprintAttrs,
                        new DiagonalDominantNbColumnColorGroupService(footprintAttrs)),
                true,
                true,
                true);

        // example of addition footprint extension point
        renderer.addPaintAfterEp(new AbsorptionClusterRendererPaintAfterEP(ohlcvDataSet, chart));
        renderer.getDatasets().addAll(ohlcvDataSet);

        chart.getRenderers().clear();
        chart.getRenderers().add(renderer);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }
}
