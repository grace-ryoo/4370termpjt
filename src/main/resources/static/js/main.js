document.addEventListener('DOMContentLoaded', function () {
    // submittable is expected to be text fields.
    var submittables = document.getElementsByClassName('submittable');

    for (var submittable of submittables) {
        // Make text field submit the enclosing form when the enter key is pressed.
        submittable.addEventListener('keydown', function (e) {
            // Check if Enter was pressed without the Shift key
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault(); // Prevent new lines.
                this.form.submit(); // Submit the form.
                console.log(this.form + ' was submitted.');
            }
        });
    }
});






// Rating functionality (basic)
document.querySelectorAll('.rating-stars button').forEach(star => {
    star.addEventListener('click', () => {
        const rating = parseInt(star.getAttribute('data-rating'));
        alert('Rated ' + rating + ' stars!');
        
        // Set all stars up to the clicked one to filled (change color or add filled class)
        const allStars = star.parentElement.querySelectorAll('button');
        allStars.forEach((s, index) => {
            s.style.color = index < rating ? '#f39c12' : '#ccc'; // Yellow for filled stars
        });
    });
});
