/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.function.marketdata.curve;

import com.opengamma.strata.basics.market.MarketDataBox;
import com.opengamma.strata.basics.market.ReferenceData;
import com.opengamma.strata.calc.CalculationMarketData;
import com.opengamma.strata.calc.marketdata.MarketDataConfig;
import com.opengamma.strata.calc.marketdata.MarketDataFunction;
import com.opengamma.strata.calc.marketdata.MarketDataRequirements;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.market.curve.Curve;
import com.opengamma.strata.market.curve.CurveGroup;
import com.opengamma.strata.market.id.CurveGroupId;
import com.opengamma.strata.market.id.CurveId;

/**
 * Market data function that locates a curve by name.
 * <p>
 * This function finds an instance of {@link Curve} using the name held in {@link CurveId}.
 * <p>
 * The curve is not actually built in this class, it is extracted from an existing {@link CurveGroup}.
 * The curve group must be available in the {@code MarketDataLookup} passed to the {@link #build} method.
 */
public class CurveMarketDataFunction
    implements MarketDataFunction<Curve, CurveId> {

  @Override
  public MarketDataRequirements requirements(CurveId id, MarketDataConfig config) {
    CurveGroupId curveGroupId = CurveGroupId.of(id.getCurveGroupName(), id.getMarketDataFeed());
    return MarketDataRequirements.builder()
        .addValues(curveGroupId)
        .build();
  }

  @Override
  public MarketDataBox<Curve> build(
      CurveId id,
      MarketDataConfig config,
      CalculationMarketData marketData,
      ReferenceData refData) {

    // find curve
    CurveGroupId curveGroupId = CurveGroupId.of(id.getCurveGroupName(), id.getMarketDataFeed());
    MarketDataBox<CurveGroup> curveGroupBox = marketData.getValue(curveGroupId);
    return curveGroupBox.map(curveGroup -> findCurve(id, curveGroup));
  }

  // finds the curve
  private Curve findCurve(CurveId id, CurveGroup curveGroup) {
    return curveGroup.findCurve(id.getCurveName())
        .orElseThrow(() -> new IllegalArgumentException(Messages.format("No curve found: {}", id.getCurveName())));
  }

  @Override
  public Class<CurveId> getMarketDataIdType() {
    return CurveId.class;
  }

}
