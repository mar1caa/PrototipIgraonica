/**
 * FAJL: booking.js
 * SVRHA: Kontroliše booking formu u browseru: format datuma, proveru termina, izbor kartica i okvirnu cenu.
 * NAPOMENA: Ako menjaš minimum naplate (12), promeni istu vrednost i u BookingService.java.
 */

// Formatiranje iznosa za srpski prikaz, npr. 10.200 RSD.
const money = n => new Intl.NumberFormat('sr-RS').format(n || 0) + ' RSD';

const form = document.getElementById('bookingForm');
const date = document.getElementById('date');
const nativeDatePicker = document.getElementById('nativeDatePicker');
const datePickerButton = document.getElementById('datePickerButton');
const slot = document.getElementById('timeSlot');
const children = document.getElementById('children');
const estimate = document.getElementById('estimate');
const note = document.getElementById('availabilityNote');
const minimumChargeNotice = document.getElementById('minimumChargeNotice');

if (form && date && slot && children && estimate) {
    // Pretvara dd/mm/yyyy iz vidljivog inputa u JavaScript Date.
    function parseDisplayDate(value) {
        const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((value || '').trim());
        if (!match) return null;

        const day = Number(match[1]);
        const month = Number(match[2]);
        const year = Number(match[3]);
        const candidate = new Date(year, month - 1, day);

        if (candidate.getFullYear() !== year || candidate.getMonth() !== month - 1 || candidate.getDate() !== day) {
            return null;
        }
        return candidate;
    }

    // API endpoint očekuje ISO yyyy-mm-dd, iako korisnik vidi dd/mm/yyyy.
    function toIsoDate(value) {
        const parsed = parseDisplayDate(value);
        if (!parsed) return null;
        const yyyy = parsed.getFullYear();
        const mm = String(parsed.getMonth() + 1).padStart(2, '0');
        const dd = String(parsed.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }

    function fromIsoDate(value) {
        const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value || '');
        return match ? `${match[3]}/${match[2]}/${match[1]}` : '';
    }

    function localIsoToday() {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }

    function syncNativePicker() {
        if (!nativeDatePicker) return;
        nativeDatePicker.value = toIsoDate(date.value) || '';
    }

    // Browser-side validacija datuma; server opet proverava zbog bezbednosti.
    function validateDateField() {
        if (!date.value) {
            date.setCustomValidity('Izaberite datum ili ga unesite u formatu dd/mm/yyyy.');
            return false;
        }

        const parsed = parseDisplayDate(date.value);
        if (!parsed) {
            date.setCustomValidity('Unesite ispravan datum u formatu dd/mm/yyyy.');
            return false;
        }

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (parsed < today) {
            date.setCustomValidity('Datum ne može biti u prošlosti.');
            return false;
        }

        date.setCustomValidity('');
        return true;
    }

    function refreshChoiceStyles() {
        document.querySelectorAll('.choice').forEach(choice => {
            const input = choice.querySelector('input');
            choice.classList.toggle('selected', Boolean(input && input.checked));
        });
    }

    // Računa OKVIRNU cenu u realnom vremenu. Ne predstavlja konačan račun.
    function calc() {
        const m = document.querySelector('input[name="menuId"]:checked');
        const es = [...document.querySelectorAll('input[name="extraIds"]:checked')];

        refreshChoiceStyles();

        const actualChildren = Number(children.value || 0);
        if (minimumChargeNotice) {
            minimumChargeNotice.hidden = !(actualChildren > 0 && actualChildren < 12);
        }

        if (!m) {
            estimate.textContent = 'Izaberite meni';
            return;
        }

        // POSLOVNO PRAVILO: minimum naplate je 12 dece.
        const billedChildren = Math.max(12, actualChildren);
        let total = Number(m.dataset.price) * billedChildren;
        es.forEach(e => total += Number(e.dataset.price));
        estimate.textContent = money(total);
    }

    // Poziva backend i onemogućava zauzete termine za izabrani datum.
    async function refreshAvailability() {
        if (!validateDateField()) {
            slot.value = '';
            note.textContent = 'Izaberite ispravan datum da proverimo termine.';
            note.className = 'availability-note availability-error';
            return;
        }

        const isoDate = toIsoDate(date.value);
        syncNativePicker();
        note.textContent = 'Proveravamo dostupnost...';
        note.className = 'availability-note';

        try {
            const res = await fetch('/booking/availability?date=' + encodeURIComponent(isoDate));
            if (!res.ok) throw new Error('availability');
            const data = await res.json();

            [...slot.options].forEach(o => {
                if (!o.value) return;
                o.disabled = data.unavailable.includes(o.value);
                o.textContent = o.value + (o.disabled ? ' — zauzeto' : ' — slobodno');
            });

            slot.value = '';
            if (data.unavailable.length) {
                note.textContent = 'Zauzeti termini su označeni i nije ih moguće izabrati.';
                note.className = 'availability-note availability-info';
            } else {
                note.textContent = 'Svi termini su trenutno slobodni za ovaj datum.';
                note.className = 'availability-note availability-ok';
            }
        } catch (e) {
            note.textContent = 'Trenutno ne možemo da proverimo dostupnost. Pokušajte ponovo.';
            note.className = 'availability-note availability-error';
        }
    }

    // Skriveni native date picker daje lep kalendar, dok roditelj i dalje vidi dd/mm/yyyy.
    if (nativeDatePicker) {
        nativeDatePicker.min = localIsoToday();
        nativeDatePicker.addEventListener('change', () => {
            if (!nativeDatePicker.value) return;
            date.value = fromIsoDate(nativeDatePicker.value);
            date.setCustomValidity('');
            refreshAvailability();
        });
    }

    if (datePickerButton && nativeDatePicker) {
        datePickerButton.addEventListener('click', () => {
            syncNativePicker();
            try {
                if (typeof nativeDatePicker.showPicker === 'function') {
                    nativeDatePicker.showPicker();
                } else {
                    nativeDatePicker.focus();
                    nativeDatePicker.click();
                }
            } catch (e) {
                nativeDatePicker.focus();
                nativeDatePicker.click();
            }
        });
    }

    document.querySelectorAll('input,select').forEach(x => x.addEventListener('change', calc));
    children.addEventListener('input', calc);

    // Automatski ubacuje / dok roditelj ručno kuca datum.
    date.addEventListener('input', () => {
        let digits = date.value.replace(/\D/g, '').slice(0, 8);
        if (digits.length > 4) {
            digits = digits.slice(0, 2) + '/' + digits.slice(2, 4) + '/' + digits.slice(4);
        } else if (digits.length > 2) {
            digits = digits.slice(0, 2) + '/' + digits.slice(2);
        }
        date.value = digits;
        date.setCustomValidity('');
        syncNativePicker();
    });

    date.addEventListener('change', refreshAvailability);
    date.addEventListener('blur', () => {
        if (date.value) validateDateField();
    });

    form.addEventListener('submit', event => {
        if (!validateDateField()) {
            event.preventDefault();
            date.reportValidity();
        }
    });

    calc();
}
