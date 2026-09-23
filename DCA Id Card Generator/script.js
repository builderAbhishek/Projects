function generateCard() {
    const name = document.getElementById('name').value;
    const rollNumber = document.getElementById('roll-number').value;
    const mobile = document.getElementById('mobile').value;
    const address = document.getElementById('address').value;
    const fatherName = document.getElementById('father-name').value;
    const studentImageInput = document.getElementById('student-image');

    const canvas = document.getElementById('idCanvas');
    const ctx = canvas.getContext('2d');
    
    const cardImage = new Image();
    cardImage.src = '1.png'; // यहाँ अपने PNG का पथ डालें

    cardImage.onload = function() {
        canvas.width = cardImage.width;
        canvas.height = cardImage.height;
        ctx.drawImage(cardImage, 0, 0);

       // नाम
    ctx.fillStyle = 'blue';
    ctx.font = 'bold 140px Georgia'; // कस्टम फ़ॉन्ट और साइज
    
    // टेक्स्ट की चौड़ाई मापें
    const textWidth = ctx.measureText(name).width;
    
    // केंद्र बिंदु
    const centerX = 922;
    const textX = centerX - (textWidth / 2); // टेक्स्ट को केंद्र में लाने के लिए X पोजिशन
    
    // टेक्स्ट को ड्रॉ करें
    ctx.fillText(name, textX, 1835); // यहाँ Y पोजिशन को अनुकूलित करें


        // रोल नंबर
        ctx.fillStyle = 'black';
        ctx.font = '100px Georgia'; // अलग फ़ॉन्ट और साइज
        ctx.fillText(rollNumber, 684, 2247);

        // मोबाइल नंबर
        ctx.fillStyle = 'black';
        ctx.font = '100px Times New Roman'; // अलग फ़ॉन्ट और साइज
        ctx.fillText(mobile, 684, 2508);

        // पता - निश्चित क्षेत्र में लिखें
        ctx.fillStyle = 'black';
        ctx.font = '80px Georgia'; // अलग फ़ॉन्ट और साइज
        
        const startX = 684; // प्रारंभिक X पोजिशन
        const startY = 2556; // प्रारंभिक Y पोजिशन
        const endX = 1760; // अंत X पोजिशन
        const endY = 2898; // अंत Y पोजिशन
        
        // बॉक्स का निर्माण
        ctx.strokeStyle = 'white'; // बॉक्स की रंग
        ctx.strokeRect(startX, startY, endX - startX, endY - startY); // बॉक्स ड्रॉ करें
        
        // टेक्स्ट को बॉक्स के अंदर लिखें
        wrapText(address, ctx, 100, startX, startY + 80, endX - startX); // 100px का padding नीचे
        
        // पिता का नाम
        ctx.fillStyle = 'black';
        ctx.font = '100px Georgia'; // अलग फ़ॉन्ट और साइज
        ctx.fillText(fatherName, 684, 2380);

        // छात्र की छवि जोड़ना
        if (studentImageInput.files && studentImageInput.files[0]) {
            const img = new Image();
            img.src = URL.createObjectURL(studentImageInput.files[0]); // अपलोड की गई छवि का URL

            img.onload = function() {
                const centerX = 922; // केंद्र X पोजिशन
                const centerY = 1200; // केंद्र Y पोजिशन
                const radius = 400; // आधा व्यास (300px का आधा)

                // वृत्त के लिए क्लिपिंग क्षेत्र बनाना
                ctx.save();
                ctx.beginPath();
                ctx.arc(centerX, centerY, radius, 0, Math.PI * 2, false);
                ctx.clip();
                
                // छवि को केंद्रित करना
                ctx.drawImage(img, centerX - radius, centerY - radius, radius * 2, radius * 2); // व्यास में छवि ड्रॉ करें
                ctx.restore(); // क्लिपिंग क्षेत्र को पुनर्स्थापित करें

                const downloadBtn = document.getElementById('downloadBtn');
                downloadBtn.style.display = 'block';
                downloadBtn.onclick = function() {
                    const link = document.createElement('a');
                    link.download = 'id_card.png';
                    link.href = canvas.toDataURL('image/png');
                    link.click();
                };
            };
        } else {
            // यदि कोई छवि अपलोड नहीं की गई है
            alert("Please upload a student image.");
        }
    };
}

// टेक्स्ट को कई लाइनों में विभाजित करने का फ़ंक्शन
function wrapText(text, context, lineHeight, x, y, maxWidth) {
    const words = text.split(' ');
    let line = '';
    
    for (let n = 0; n < words.length; n++) {
        const testLine = line + words[n] + ' ';
        const metrics = context.measureText(testLine);
        const testWidth = metrics.width;

        if (testWidth > maxWidth && n > 0) {
            context.fillText(line, x, y);
            line = words[n] + ' ';
            y += lineHeight;
        } else {
            line = testLine;
        }
    }
    context.fillText(line, x, y);
}
