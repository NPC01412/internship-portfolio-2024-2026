using Microsoft.AspNetCore.Mvc;
using Partice.Data;
using Partice.Models;
using X.PagedList.Extensions;

namespace Partice.Controllers;

public class FinalController : Controller
{
    private readonly DemoOrderStore store;
    public FinalController(DemoOrderStore store) => this.store = store;

    public IActionResult Prog1(int? page = 1)
    {
        const int pageSize = 10;
        var all = store.All().OrderByDescending(x => x.portnumber).ToList();
        int lastPage = Math.Max(1, (all.Count + pageSize - 1) / pageSize);
        int currentPage = Math.Clamp(page ?? 1, 1, lastPage);
        var paged = all.ToPagedList(currentPage, pageSize);
        ViewBag.userModel = paged;
        return View(paged.ToList());
    }

    [HttpGet]
    public IActionResult Prog2()
    {
        ViewBag.District = store.All().Select(x => x.zone).Distinct().OrderBy(x => x).ToList();
        return View();
    }

    [HttpPost]
    public IActionResult Prog2(string district)
        => View("ShowDistrictResult", store.All().Where(x => x.zone == district).ToList());

    public IActionResult ShowDistrictResult() => View(new List<Order>());
    public IActionResult Prog3() => View(store.All());

    [HttpGet]
    public IActionResult Create() => View();

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult Create([Bind("member,EmID,goodsway,location,city,zone,portnumber")] Order order)
    {
        if (!ModelState.IsValid) return View(order);
        store.Add(order);
        return RedirectToAction(nameof(Prog3));
    }

    [HttpGet]
    public IActionResult Edit(int? id)
    {
        if (!id.HasValue) return NotFound();
        var order = store.Find(id.Value);
        return order is null ? NotFound() : View(order);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult Edit(int id, [Bind("ID,member,EmID,goodsway,location,city,zone,portnumber")] Order order)
    {
        if (id != order.ID) return NotFound();
        if (!ModelState.IsValid) return View(order);
        return store.Update(order) ? RedirectToAction(nameof(Prog3)) : NotFound();
    }

    [HttpPost, ActionName("Delete")]
    [ValidateAntiForgeryToken]
    public IActionResult DeleteConfirmed(int id)
    {
        store.Delete(id);
        return RedirectToAction(nameof(Prog3));
    }

    [HttpGet] public IActionResult Prog4() => View();
    [HttpGet] public IActionResult Query() => View(new List<Order>());
    [HttpPost] public IActionResult Query(string city, string location)
        => View(store.All().Where(x => x.city == city && x.location == location).ToList());
    [HttpGet] public IActionResult Prog5() => View();
    [HttpGet] public IActionResult Query2() => View(new List<Order>());
    [HttpPost] public IActionResult Query2(string? location)
        => View(store.All().Where(x => (x.location ?? "").Contains(location ?? "", StringComparison.OrdinalIgnoreCase)).ToList());

    [HttpGet]
    public IActionResult Prog6()
    {
        if (HttpContext.Session.GetString("Id") is null) TempData["message"] = "please login!";
        return View();
    }

    [HttpGet]
    public IActionResult Prog7()
    {
        HttpContext.Session.SetString("Id", "Logined");
        TempData["message"] = "Logged in!";
        return View();
    }
}
