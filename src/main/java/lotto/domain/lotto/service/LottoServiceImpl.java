package lotto.domain.lotto.service;

import camp.nextstep.edu.missionutils.Randoms;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lotto.domain.lotto.domain.LottoGame;
import lotto.domain.lotto.dto.request.LottoGameReq;
import lotto.domain.lotto.domain.LottoResult;

public class LottoServiceImpl implements LottoService{

    private LottoGame lottoGame;

    @Override
    public void createAndPlayGame(LottoGameReq request) {
        List<List<Integer>> purchasedLottos = createPurchasedLottos(request);
        this.lottoGame = LottoGame.of(
                purchasedLottos,
                request.getWinningNumbers(),
                request.getBonusNumber(),
                request.getCost()
        );
        this.lottoGame.calculateResults();
    }

    @Override
    public LottoResult getGameResult() {
        validateGameExists();

        Map<LottoResult, Integer> results = lottoGame.getResults();
        double profitRate = calculateProfitRate(results, this.lottoGame.getCost());


        return null;
    }

    /**
     * 로또 구입
     */

    private List<List<Integer>> createPurchasedLottos(LottoGameReq request) {
        validatePurchaseAmount(request.getCost());

        int lottoCount = request.getCost() / 1000;
        List<List<Integer>> purchasedLottos = new ArrayList<>();

        for (int i = 0; i < lottoCount; i++) {
            List<Integer> lotto = Randoms.pickUniqueNumbersInRange(1, 45, 6);
            purchasedLottos.add(new ArrayList<>(lotto));
        }

        return purchasedLottos;
    }

    private void validatePurchaseAmount(int cost) {
        if (cost < 1000) {
            throw new IllegalArgumentException("[ERROR] 로또 구입 금액은 1,000원 이상이어야 합니다.");
        }
        if (cost % 1000 != 0) {
            throw new IllegalArgumentException("[ERROR] 로또 구입은 1,000원 단위로 가능합니다.");
        }
    }

    /**
     * 수익율 계산
     */

    private double calculateProfitRate(Map<LottoResult, Integer> results, int cost) {
        final long totalPrize = calculateTotalPrize(results);
        double rate = (totalPrize * 100.0) / cost;
        return Math.round(rate * 10) / 10.0;
    }

    private long calculateTotalPrize(Map<LottoResult, Integer> results) {
        long totalPrize = 0;
        for (Map.Entry<LottoResult, Integer> entry : results.entrySet()) {
            totalPrize += (long) entry.getKey().getPrize() * entry.getValue();
        }
        return totalPrize;
    }

    /**
     * Validate
     */

    private void validateGameExists() {
        if (this.lottoGame == null) {
            throw new IllegalStateException("[ERROR] 게임이 시작되지 않았습니다.");
        }
    }
}
