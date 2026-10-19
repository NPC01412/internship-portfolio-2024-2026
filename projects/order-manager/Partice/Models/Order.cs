using System.ComponentModel.DataAnnotations;

namespace Partice.Models
{
    public class Order
    {
        [Key]
        [Required(ErrorMessage = "請輸入ID")]
        public int ID { get; set; }

        [Required(ErrorMessage = "請輸入member")]
        public string? member { get; set; }

        [Required(ErrorMessage = "EmID")]
        public int EmID { get; set; }

        [Required(ErrorMessage = "請輸入goodsway")]
        public int goodsway { get; set; }

        [Required(ErrorMessage = "請輸入location")]
        public string? location { get; set; }

        [Required(ErrorMessage = "請輸入city")]
        public string? city { get; set; }

        [Required(ErrorMessage = "請輸入zone")]
        public string? zone { get; set; }

        [Required(ErrorMessage = "請輸入portnumber")]
        public string? portnumber { get; set; }


    }
}
