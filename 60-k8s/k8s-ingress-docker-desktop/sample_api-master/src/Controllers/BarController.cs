using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using routing.Models;

namespace routing.Controllers
{
    [ApiController]
    [Route("bar")]
    public class BarController : ControllerBase {
        private readonly ILogger<BarController> _logger;

        public BarController(ILogger<BarController> logger)
        {
            _logger = logger;
        }

        [Route("{reference:guid}")]
        public async Task<Bar> GetBar(Guid reference) {
            var bar = new Bar(reference.ToString());
            return bar;
        }
    }
}