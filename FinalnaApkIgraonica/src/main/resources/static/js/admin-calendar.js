/**
 * FAJL: admin-calendar.js
 * SVRHA: Crta mesečni admin kalendar od rezervacija koje backend ubaci u stranicu.
 * NAPOMENA: Ovde menjaj ponašanje kalendara, nazive meseci, boje/status prikaz ili navigaciju.
 */

(() => {
  const calendar = document.getElementById('adminCalendar');
  if (!calendar) return;

  const monthLabel = document.getElementById('calendarMonthLabel');
  const prevButton = document.getElementById('calendarPrev');
  const nextButton = document.getElementById('calendarNext');
  const todayButton = document.getElementById('calendarToday');

  // Nazivi meseci koji se prikazuju u admin kalendaru.
const monthNames = [
    'Januar', 'Februar', 'Mart', 'April', 'Maj', 'Jun',
    'Jul', 'Avgust', 'Septembar', 'Oktobar', 'Novembar', 'Decembar'
  ];
  const weekdayNames = ['Pon', 'Uto', 'Sre', 'Čet', 'Pet', 'Sub', 'Ned'];

  const sourceNodes = [...document.querySelectorAll('.calendar-booking-source')];
  const bookings = sourceNodes.map(node => ({
    date: node.dataset.date,
    status: node.dataset.status,
    time: node.dataset.time || '',
    child: node.dataset.child || 'Rođendan',
    parent: node.dataset.parent || '',
    url: node.dataset.url || '#'
  }));

  const now = new Date();
  let shownYear = now.getFullYear();
  let shownMonth = now.getMonth();

  function dateKey(year, month, day) {
    return `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
  }

  function isToday(year, month, day) {
    return year === now.getFullYear() && month === now.getMonth() && day === now.getDate();
  }

  function mondayIndex(jsDay) {
    return (jsDay + 6) % 7;
  }

  function eventMarkup(booking) {
    const statusClass = booking.status === 'CONFIRMED' ? 'confirmed' : 'pending';
    const statusText = booking.status === 'CONFIRMED' ? 'Potvrđeno' : 'Na čekanju';
    const startTime = booking.time.split('-')[0].trim();
    return `
      <a class="calendar-event ${statusClass}" href="${booking.url}" title="${statusText} · ${booking.parent}">
        <span class="calendar-event-time">${startTime}</span>
        <span class="calendar-event-name">${booking.child}</span>
      </a>`;
  }

  function render() {
    monthLabel.textContent = `${monthNames[shownMonth]} ${shownYear}`;
    calendar.innerHTML = '';

    weekdayNames.forEach(name => {
      const header = document.createElement('div');
      header.className = 'calendar-weekday';
      header.textContent = name;
      calendar.appendChild(header);
    });

    const firstDay = new Date(shownYear, shownMonth, 1);
    const daysInMonth = new Date(shownYear, shownMonth + 1, 0).getDate();
    const leadingEmpty = mondayIndex(firstDay.getDay());
    const totalCells = Math.ceil((leadingEmpty + daysInMonth) / 7) * 7;

    for (let cell = 0; cell < totalCells; cell++) {
      const day = cell - leadingEmpty + 1;
      const dayCell = document.createElement('div');
      dayCell.className = 'calendar-day';

      if (day < 1 || day > daysInMonth) {
        dayCell.classList.add('calendar-day-empty');
        calendar.appendChild(dayCell);
        continue;
      }

      const key = dateKey(shownYear, shownMonth, day);
      const dayBookings = bookings
        .filter(booking => booking.date === key)
        .sort((a, b) => a.time.localeCompare(b.time));

      if (isToday(shownYear, shownMonth, day)) dayCell.classList.add('calendar-day-today');
      if (dayBookings.length) dayCell.classList.add('calendar-day-has-events');

      const number = document.createElement('div');
      number.className = 'calendar-day-number';
      number.innerHTML = `<span>${day}</span>${isToday(shownYear, shownMonth, day) ? '<small>Danas</small>' : ''}`;
      dayCell.appendChild(number);

      const eventsWrap = document.createElement('div');
      eventsWrap.className = 'calendar-events';
      eventsWrap.innerHTML = dayBookings.map(eventMarkup).join('');
      dayCell.appendChild(eventsWrap);

      calendar.appendChild(dayCell);
    }
  }

  prevButton?.addEventListener('click', () => {
    shownMonth--;
    if (shownMonth < 0) { shownMonth = 11; shownYear--; }
    render();
  });

  nextButton?.addEventListener('click', () => {
    shownMonth++;
    if (shownMonth > 11) { shownMonth = 0; shownYear++; }
    render();
  });

  todayButton?.addEventListener('click', () => {
    shownYear = now.getFullYear();
    shownMonth = now.getMonth();
    render();
  });

  render();
})();
