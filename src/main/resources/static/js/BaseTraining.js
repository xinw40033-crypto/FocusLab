/**
 * 基础训练类
 * 所有训练类型的基类
 */
class BaseTraining {
    constructor(content, imageElement, canvasElement) {
        this.content = content;
        this.image = imageElement;
        this.canvas = canvasElement;
        this.ctx = canvasElement.getContext('2d');
        this.answers = [];
        this.countdownTime = null; // 倒计时总时长（秒）
        this.countdownInterval = null; // 倒计时定时器
        this.countdownRemaining = 0; // 倒计时剩余时间（秒）
        this.onCountdownEnd = null; // 倒计时结束回调
    }

    init() {
        // 子类实现具体初始化逻辑
    }

    getAnswers() {
        return this.answers;
    }

    clear() {
        this.answers = [];
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.updateAnswerList();
    }

    /**
     * 设置倒计时
     * @param {number} seconds - 倒计时秒数
     * @param {function} callback - 倒计时结束回调函数
     */
    setCountdown(seconds, callback) {
        this.countdownTime = seconds;
        this.countdownRemaining = seconds;
        this.onCountdownEnd = callback;
        
        const countdownElement = document.getElementById('countdown-timer');
        if (countdownElement && seconds > 0) {
            countdownElement.style.display = 'inline';
            this.updateCountdownDisplay();
            this.startCountdown();
        }
    }

    /**
     * 启动倒计时
     */
    startCountdown() {
        if (this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }
        
        this.countdownInterval = setInterval(() => {
            this.countdownRemaining--;
            this.updateCountdownDisplay();
            
            // 最后10秒添加警告样式
            const countdownElement = document.getElementById('countdown-timer');
            if (this.countdownRemaining <= 10 && this.countdownRemaining > 0) {
                countdownElement.classList.add('countdown-warning');
            }
            
            if (this.countdownRemaining <= 0) {
                this.stopCountdown();
                if (this.onCountdownEnd) {
                    this.onCountdownEnd();
                }
            }
        }, 1000);
    }

    /**
     * 停止倒计时
     */
    stopCountdown() {
        if (this.countdownInterval) {
            clearInterval(this.countdownInterval);
            this.countdownInterval = null;
        }
    }

    /**
     * 更新倒计时显示
     */
    updateCountdownDisplay() {
        const countdownElement = document.getElementById('countdown-timer');
        if (!countdownElement) return;
        
        const minutes = Math.floor(this.countdownRemaining / 60);
        const seconds = this.countdownRemaining % 60;
        const timeStr = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        countdownElement.textContent = `⏱ ${timeStr}`;
    }

    /**
     * 获取倒计时配置
     * 根据训练类型和难度返回不同的倒计时时长
     */
    getCountdownConfig() {
        // 默认倒计时配置（秒）
        const defaultConfig = {
            1: 120,  // 低难度：2分钟
            2: 90,   // 中难度：1.5分钟
            3: 60    // 高难度：1分钟
        };
        
        // 从content.config中获取难度，默认为1
        const difficulty = this.content.difficulty || 1;
        return defaultConfig[difficulty] || 120;
    }

    updateAnswerList() {
        const answerList = document.getElementById('answer-list');
        if (this.answers.length === 0) {
            answerList.innerHTML = '<p class="text-muted">暂无答案</p>';
        } else {
            answerList.innerHTML = this.answers.map((ans, idx) => 
                `<div class="mb-1">
                    <span class="badge bg-primary">${idx + 1}</span> 
                    ${this.formatAnswer(ans)}
                </div>`
            ).join('');
        }
    }

    formatAnswer(answer) {
        if (Array.isArray(answer)) {
            return `[${answer.join(', ')}]`;
        }
        return JSON.stringify(answer);
    }

    getRelativePosition(event) {
        const rect = this.image.getBoundingClientRect();
        return {
            x: Math.round(event.clientX - rect.left),
            y: Math.round(event.clientY - rect.top)
        };
    }
}
