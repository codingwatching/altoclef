package adris.altoclef.util.progresscheck;

import adris.altoclef.util.csharpisbetter.Timer;

/**
 * Simple progress checker that requires we always make progress.
 */
public class LinearProgressChecker implements IProgressChecker<Double> {

    private final double _minProgress;
    private final Timer _timer;

    private double _lastProgress;
    private double _currentProgress;

    private boolean _first;

    private boolean _failed;

    public LinearProgressChecker(double timeout, double minProgress) {
        _minProgress = minProgress;
        _timer = new Timer(timeout);
        reset();
    }

    @Override
    public void setProgress(Double progress) {
        _currentProgress = progress;
        if (_timer.elapsed()) {
            if (!_first) {
                double improvement = progress - _lastProgress;
                if (improvement < _minProgress) {
                    _failed = true;
                }
            }
            _first = false;
            _timer.reset();
            _lastProgress = progress;
        }
    }

    @Override
    public boolean failed() {
        return _failed;
    }

    public void reset() {
        //_first = true;
        _failed = false;
        _timer.reset();
        _lastProgress = _currentProgress;
    }
}
