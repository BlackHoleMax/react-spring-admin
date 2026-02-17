import React, { useCallback, useEffect, useState } from 'react';
import './DayNightToggle.css';

export interface DayNightToggleProps {
  /** 当前是否为夜间模式 */
  isDarkMode?: boolean;
  /** 切换模式时的回调函数 */
  onToggle?: (isDarkMode: boolean) => void;
  /** 是否禁用 */
  disabled?: boolean;
  /** 自定义类名 */
  className?: string;
}

/**
 * 昼夜切换按钮组件 - 与原网页效果保持一致
 */
const DayNightToggle: React.FC<DayNightToggleProps> = ({
  isDarkMode: externalMode,
  onToggle,
  disabled = false,
  className = '',
}) => {
  const [internalMode, setInternalMode] = useState<boolean>(false);
  const [isAnimating, setIsAnimating] = useState<boolean>(false);

  const isDark = externalMode !== undefined ? externalMode : internalMode;

  useEffect(() => {
    if (externalMode !== undefined) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setInternalMode(externalMode);
    }
  }, [externalMode]);

  const handleClick = useCallback(() => {
    if (disabled || isAnimating) return;

    const newMode = !isDark;

    setIsAnimating(true);

    setInternalMode(newMode);

    if (onToggle) {
      onToggle(newMode);
    }

    setTimeout(() => {
      setIsAnimating(false);
    }, 500);
  }, [disabled, isAnimating, isDark, onToggle]);

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (disabled || isAnimating) return;

      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        handleClick();
      }
    },
    [disabled, isAnimating, handleClick]
  );

  const containerClasses = [
    'day-night-toggle',
    isDark ? 'dark' : 'light',
    disabled ? 'disabled' : '',
    isAnimating ? 'animating' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div
      className={containerClasses}
      onClick={handleClick}
      onKeyDown={handleKeyDown}
      role="switch"
      aria-checked={isDark}
      aria-label={`切换为${isDark ? '夜间' : '日间'}模式`}
      tabIndex={disabled ? -1 : 0}
      data-testid="day-night-toggle"
    >
      <div className="toggle-container">
        <div className="bg-shapes">
          <div className="cloud c1"></div>
          <div className="cloud c2"></div>
          <div className="cloud c3"></div>

          <div className="star s1"></div>
          <div className="star s2"></div>
          <div className="star s3"></div>
          <div className="star s4"></div>
          <div className="star s5"></div>
          <div className="star s6"></div>
        </div>

        <div className="toggle-slider">
          <div className={`slider ${isDark ? 'night' : 'day'}`}>
            <div className="sun-icon">
              <div className="sun-core"></div>
              <div className="sun-ray r1"></div>
              <div className="sun-ray r2"></div>
              <div className="sun-ray r3"></div>
              <div className="sun-ray r4"></div>
              <div className="sun-ray r5"></div>
              <div className="sun-ray r6"></div>
              <div className="sun-ray r7"></div>
              <div className="sun-ray r8"></div>
            </div>

            <div className="moon-icon">
              <div className="moon-body"></div>
              <div className="moon-crater c1"></div>
              <div className="moon-crater c2"></div>
              <div className="moon-crater c3"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DayNightToggle;
