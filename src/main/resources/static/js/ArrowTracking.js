/**
 * 箭头追踪训练类
 * 用户需要按照路径顺序点击图片
 */
class ArrowTracking extends BaseTraining {
    constructor(content, imageElement, canvasElement) {
        super(content, imageElement, canvasElement);
        this.userPath = [];
        this.config = JSON.parse(content.config);
    }

    init() {
        this.image.addEventListener('click', (e) => this.onImageClick(e));
        this.updateAnswerList();
    }

    onImageClick(event) {
        const pos = this.getRelativePosition(event);
        this.userPath.push([pos.x, pos.y]);
        this.answers.push([pos.x, pos.y]);
        
        this.drawPath();
        this.updateAnswerList();
    }

    drawPath() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        if (this.userPath.length === 0) return;

        // 绘制路径点
        this.userPath.forEach((point, index) => {
            this.ctx.beginPath();
            this.ctx.arc(point[0], point[1], 5, 0, Math.PI * 2);
            this.ctx.fillStyle = index === this.userPath.length - 1 ? '#dc3545' : '#007bff';
            this.ctx.fill();
            
            // 绘制点的序号
            this.ctx.fillStyle = '#fff';
            this.ctx.font = 'bold 10px Arial';
            this.ctx.textAlign = 'center';
            this.ctx.textBaseline = 'middle';
            this.ctx.fillText(index + 1, point[0], point[1]);
        });

        // 绘制连线
        if (this.userPath.length > 1) {
            this.ctx.beginPath();
            this.ctx.moveTo(this.userPath[0][0], this.userPath[0][1]);
            for (let i = 1; i < this.userPath.length; i++) {
                this.ctx.lineTo(this.userPath[i][0], this.userPath[i][1]);
            }
            this.ctx.strokeStyle = '#28a745';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        }
    }

    clear() {
        this.userPath = [];
        super.clear();
    }

    formatAnswer(answer) {
        return `(${answer[0]}, ${answer[1]})`;
    }
}
