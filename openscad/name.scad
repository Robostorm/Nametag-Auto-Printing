// Name plate generator. Feel free to edit any of the values!

// Your name
name = "name";
chars = 4;

// The thicknes of the text (Amount text sticks up by)
textthick = 1;
// The height of the text, if looking down from the top
textheight = 7;

// Length of the flat part
length = (textheight*(2/3))*chars+1;
// Width of the flat part
width = 10;
// Thickness of the flat part
thickness = 2;

$fn = 20;

translate([-(width + length)/2, -width/2, 0]){
	difference(){
		union(){
			// Make the flat part
            translate([width/2, 0, 0]){
                cube([length + width/2, width, thickness]);
            }

			translate([width/2, width/2, 0]){
				cylinder(r=width/2, h=thickness);
			}

			//Move above the flat part
			translate([width-1, width/2, thickness]){
				// Make the text 3d
				color("yellow") linear_extrude(textthick, convexity=10){
					// Draw the text
					text(name, textheight, valign="center", font="Ubuntu Mono:style=Bold");
				}
			}
		}

		translate([width/2, width/2, -1]){
			cylinder(r=width/2-thickness, h=thickness+2);
		}
	}
}
