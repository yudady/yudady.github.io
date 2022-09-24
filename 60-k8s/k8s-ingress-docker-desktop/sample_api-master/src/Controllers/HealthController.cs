using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace routing.Controllers
{
    [ApiController]
    [Route("")]
    public class HealthController : ControllerBase {
        private readonly ILogger<HealthController> _logger;

        public HealthController(ILogger<HealthController> logger)
        {
            _logger = logger;
        }

        [HttpGet]
        [Route("")]
        public async Task<string> GetHealth() {
            const string Message = "I am healthy";
            return Message;
        }
    }
}