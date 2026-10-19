using System.Text.Json;
using Partice.Models;

namespace Partice.Data;

// The public demo stores generated examples locally. The coursework used SQL Server.
public sealed class DemoOrderStore
{
    private readonly object gate = new();
    private readonly string filePath;
    private List<Order> orders;

    public DemoOrderStore(string filePath)
    {
        this.filePath = filePath;
        Directory.CreateDirectory(Path.GetDirectoryName(filePath)!);
        orders = File.Exists(filePath)
            ? JsonSerializer.Deserialize<List<Order>>(File.ReadAllText(filePath)) ?? new()
            : new();
        if (orders.Count == 0)
        {
            string[] zones = { "東區", "西區", "南區", "北區" };
            orders = Enumerable.Range(1, 24).Select(number => new Order
            {
                ID = number,
                member = $"範例會員 {number:00}",
                EmID = 2000 + number,
                goodsway = number % 3 + 1,
                location = $"範例路 {number} 號",
                city = "示範市",
                zone = zones[(number - 1) % zones.Length],
                portnumber = $"P{number:0000}"
            }).ToList();
            Save();
        }
    }

    public List<Order> All()
    {
        lock (gate) return orders.OrderBy(x => x.ID).ToList();
    }

    public Order? Find(int id)
    {
        lock (gate) return orders.FirstOrDefault(x => x.ID == id);
    }

    public void Add(Order order)
    {
        lock (gate)
        {
            order.ID = orders.Count == 0 ? 1 : orders.Max(x => x.ID) + 1;
            orders.Add(order);
            Save();
        }
    }

    public bool Update(Order order)
    {
        lock (gate)
        {
            int index = orders.FindIndex(x => x.ID == order.ID);
            if (index < 0) return false;
            orders[index] = order;
            Save();
            return true;
        }
    }

    public bool Delete(int id)
    {
        lock (gate)
        {
            int removed = orders.RemoveAll(x => x.ID == id);
            if (removed == 0) return false;
            Save();
            return true;
        }
    }

    private void Save()
    {
        File.WriteAllText(filePath, JsonSerializer.Serialize(orders, new JsonSerializerOptions { WriteIndented = true }));
    }
}
