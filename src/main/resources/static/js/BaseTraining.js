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
