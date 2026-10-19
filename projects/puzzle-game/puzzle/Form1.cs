using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.IO;
using System.Windows.Forms;

namespace puzzle
{
    public partial class Form1 : Form
    {
        private Bitmap fullImage;
        private int rows = 3, cols = 3;
        private Bitmap[,] puzzlePieces;
        private Dictionary<PictureBox, Point> correctPositionsMap = new Dictionary<PictureBox, Point>(); // 存儲拼圖的正確位置
        private List<Point> shuffledPositions = new List<Point>(); // 存儲打亂後的位置
        private Random rand = new Random();
        private PictureBox draggingPiece = null;
        private Point offset;
        private int animationStep = 10;
        private int activeAnimations = 0;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            // 顯示選擇拼圖大小的對話框
            DialogResult result = MessageBox.Show("請選擇拼圖大小:\n\n 是 - 3x3\n 否 - 5x5\n 取消 - 10x10","選擇拼圖大小",MessageBoxButtons.YesNoCancel,MessageBoxIcon.Question);

            if (result == DialogResult.Yes)
            {
                rows = 3;
                cols = 3;
            }
            else if (result == DialogResult.No)
            {
                rows = 5;
                cols = 5;
            }
            else if (result == DialogResult.Cancel)
            {
                rows = 10;
                cols = 10;
            }

            // 隨機選擇圖片
            string[] images = { "img1.jpg", "img2.jpg", "img3.jpg" ,"img4.jpg","img5.jpg"};
            string selectedImage = images[rand.Next(images.Length)];

            LoadAndSplitImage(selectedImage);

            // 設定視窗大小與圖片相同
            this.ClientSize = new Size(fullImage.Width, fullImage.Height);

            // 鎖定視窗大小，防止調整
            this.FormBorderStyle = FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;

            MessageBox.Show($"歡迎來到拼圖遊戲！\n\n本次的拼圖大小為 {rows} × {cols}。\n圖片：{selectedImage}\n按住滑鼠左鍵拖曳拼圖塊來交換位置，完成後會顯示提示。","遊戲規則",MessageBoxButtons.OK,MessageBoxIcon.Information);

            ShufflePieces();
            RenderPuzzle();
        }

        private void LoadAndSplitImage(string imagePath)
        {
            string fullPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "assets", imagePath);
            Bitmap original = new Bitmap(fullPath);
            int width = (original.Width / cols) * cols;
            int height = (original.Height / rows) * rows;
            // 保持每塊尺寸一致；最多裁掉圖片右側或底部的 9 個像素。
            if (width != original.Width || height != original.Height)
            {
                fullImage = original.Clone(new Rectangle(0, 0, width, height), original.PixelFormat);
                original.Dispose();
            }
            else
            {
                fullImage = original;
            }
            int pieceWidth = fullImage.Width / cols;
            int pieceHeight = fullImage.Height / rows;

            puzzlePieces = new Bitmap[rows, cols];

            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    puzzlePieces[i, j] = fullImage.Clone(
                        new Rectangle(j * pieceWidth, i * pieceHeight, pieceWidth, pieceHeight),
                        fullImage.PixelFormat);
                }
            }
        }

        private void ShufflePieces()
        {
            List<Point> correctPositions = new List<Point>();

            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    correctPositions.Add(new Point(j * (fullImage.Width / cols), i * (fullImage.Height / rows)));
                }
            }

            shuffledPositions = new List<Point>(correctPositions);
            for (int i = shuffledPositions.Count - 1; i > 0; i--)
            {
                int j = rand.Next(i + 1);
                Point temp = shuffledPositions[i];
                shuffledPositions[i] = shuffledPositions[j];
                shuffledPositions[j] = temp;
            }
            if (shuffledPositions.SequenceEqual(correctPositions))
            {
                Point temp = shuffledPositions[0];
                shuffledPositions[0] = shuffledPositions[1];
                shuffledPositions[1] = temp;
            }
        }

        private void RenderPuzzle()
        {
            this.Controls.Clear();
            int pieceWidth = fullImage.Width / cols;
            int pieceHeight = fullImage.Height / rows;

            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    PictureBox pb = new PictureBox
                    {
                        Width = pieceWidth,
                        Height = pieceHeight,
                        Location = shuffledPositions[i * cols + j], // 使用已打亂的位置
                        Image = puzzlePieces[i, j],
                        Tag = shuffledPositions[i * cols + j]
                    };

                    correctPositionsMap[pb] = new Point(j * pieceWidth, i * pieceHeight); // 存儲拼圖的正確位置

                    pb.MouseDown += PictureBox_MouseDown;
                    pb.MouseMove += PictureBox_MouseMove;
                    pb.MouseUp += PictureBox_MouseUp;

                    this.Controls.Add(pb);
                }
            }
        }

        private void PictureBox_MouseDown(object sender, MouseEventArgs e)
        {
            if (activeAnimations > 0 || e.Button != MouseButtons.Left) return;
            draggingPiece = sender as PictureBox;
            offset = new Point(e.X, e.Y);
        }

        private void PictureBox_MouseMove(object sender, MouseEventArgs e)
        {
            if (draggingPiece != null)
            {
                draggingPiece.Left = e.X + draggingPiece.Left - offset.X;
                draggingPiece.Top = e.Y + draggingPiece.Top - offset.Y;
            }
        }

        private void PictureBox_MouseUp(object sender, MouseEventArgs e)
        {
            if (draggingPiece == null) return;

            Point aOriginalLocation = (Point)draggingPiece.Tag;

            Point closestGrid = shuffledPositions
                .OrderBy(p => Distance(p, draggingPiece.Location))
                .First();

            PictureBox bPiece = this.Controls.OfType<PictureBox>()
                .FirstOrDefault(pb => pb != draggingPiece && (Point)pb.Tag == closestGrid);

            if (bPiece != null)
            {
                StartAnimation(draggingPiece, closestGrid);
                draggingPiece.Tag = closestGrid;

                StartAnimation(bPiece, aOriginalLocation);
                bPiece.Tag = aOriginalLocation;
            }
            else
            {
                StartAnimation(draggingPiece, closestGrid);
                draggingPiece.Tag = closestGrid;
            }

            draggingPiece = null;

        }

        private void StartAnimation(PictureBox pb, Point target)
        {
            activeAnimations++;
            Timer timer = new Timer { Interval = 10 };
            timer.Tick += (s, e) =>
            {
                int dx = target.X - pb.Left;
                int dy = target.Y - pb.Top;

                if (Math.Abs(dx) <= 1 && Math.Abs(dy) <= 1)
                {
                    pb.Location = target;
                    timer.Stop();
                    timer.Dispose();
                    activeAnimations--;
                    if (activeAnimations == 0) CheckPuzzleCompletion();
                }
                else
                {
                    pb.Left += Math.Sign(dx) * Math.Min(Math.Abs(dx), animationStep);
                    pb.Top += Math.Sign(dy) * Math.Min(Math.Abs(dy), animationStep);
                }
            };
            timer.Start();
        }

        private double Distance(Point a, Point b)
        {
            return Math.Sqrt(Math.Pow(a.X - b.X, 2) + Math.Pow(a.Y - b.Y, 2));
        }

        private void CheckPuzzleCompletion()
        {
            bool isCompleted = this.Controls.OfType<PictureBox>().All(pb =>
            {
                return pb.Location == correctPositionsMap[pb]; // 確保拼圖回到正確位置
            });

            if (isCompleted)
            {
                MessageBox.Show("恭喜完成!", "完成", MessageBoxButtons.OK, MessageBoxIcon.Information);

                foreach (PictureBox pb in this.Controls.OfType<PictureBox>())
                {
                    pb.Enabled = false; // 禁止拼圖塊再被拖曳
                }
            }
        }
    }
}
