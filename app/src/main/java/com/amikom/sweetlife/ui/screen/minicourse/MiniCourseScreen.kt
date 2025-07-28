package com.amikom.sweetlife.ui.screen.minicourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.amikom.sweetlife.R

data class MiniCourse(
    val title: String,
    val imageUrl: String,
    val link: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniCourseScreen(courses: List<MiniCourse> = sampleCourses()) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mini Course") })
        }
    ) { safeDrawingPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeDrawingPadding)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(courses) { course ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        uriHandler.openUri(course.link)
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        // Replace with AsyncImage if using Coil/Glide
                        AsyncImage(
                            model = course.imageUrl,
                            contentDescription = course.title,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.bapak),
                            error = painterResource(id = R.drawable.bapak)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(course.title, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

fun sampleCourses() = listOf(
    MiniCourse(
        title = "Healthy Eating 101",
        imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMTEhUTExMWFRUXFxcWFxgXGBcaGhgYFxcXGBcXGBgaHSgiGB0lHRcVITEhJSkrLi4uFx8zODMtNygtLisBCgoKDg0OGxAQGi0lICUtLS0tLS0tLS0vLS0tLy0tLS0tLS0tLS0tLS0tLS0vLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAKgBLAMBIgACEQEDEQH/xAAbAAABBQEBAAAAAAAAAAAAAAAGAQIDBAUAB//EAD8QAAEDAgMFBgQFAQgCAwEAAAEAAhEDIQQSMQVBUWFxBiKBkaHwEzKx0UJSweHxYgcUIzNygqLCkrIXk+IW/8QAGgEAAgMBAQAAAAAAAAAAAAAAAQQAAgMFBv/EADIRAAICAQMDAQcCBgMBAAAAAAABAhEDBBIhMUFRcQUTIjJhobGBkRQzQsHh8TRi0RX/2gAMAwEAAhEDEQA/APVyV0riUkKhY6UidCSECCWXWXQuUCLK6UhSQoQUlJPNdCQoBOlckJTUAji5JCQkpslCw0PhMcEhB4lJk5lSwiidy4u4pMvP0SGeX0UAIeqTRKmoEHSuI8U31TSiQ4jqEgcVyQoEFLzwTS8HcuKZ7uESCkA+4TfhncV0D+F2nFCiCPYeAUTgOEKUuStqclKQbZBHNdk6Kckb1FkEoUSxL8EjlKafAn6poplGgWQuASBg4qRzTw9Uws5IUGxrqSaWc/fmnnqUgd0KBAjyroSLlqZiwuhIoMVjGU/ncBwGpPQeBUA+OpYhJCyWdoaJcW98HW7YG/y0lQ4jtIxvytJtMTcj/SATw80Su+Pk2yE0hDJ7VybNa4b4dppy5hdS7VUye8x7ekO8dyo5q6NowclceQjKQyqOD2hSqfI8HlofI3VqUbJTRIJXQUxLCBBcqSE0lQ1Kh4pTPrMeHh8vwbY8MplghJPv2VQe8jn1UP8AenD8RSP/ANnGnUotDH8FJrhmqX803OVlVtsBgzPkN3uFwOZAuArFHaDXAFpBB0IuD4rp4s0Msd0HaFZ4pQdSRcJ6eS7MoWYjmE/OtLKUOldmCZmC7MPcI2Sh5TEhITSpZKHwkI9/wU0dfVIVLBR2RVMbinMMMaXneZhrRxJv5AeIVxpWnsqn/h5t7yXeGg9ArRjudIjltVsGBtI72g9FNTxzTy9EQ4jZtJ/zME8RY+YWdX7Ot/A8jqAR+h+qLw5F05CsuJ9eCs1wOh/VLE8FUxGxq7PlE82n9DH6qhWx76UZwZJAALTN5uRExbWD4rP4k6aLbY1akjbyroPNZuG2w134T1aQ/wA8t2+MK7RxTH3a8HxCsUJHE/zZcHHgnwmuCgBM44JkjgpOiZf3Klho20q6yp7WxYpUy4fMe63qfsAT4Kxm3XJk9otsFksY7LHzOETzAO7cJ57kKYrFVXkgHM4tbcO1m9yTMGYjXTqHY+TJz5bEyRMGxmeEcfW0VCxzAASXElxGZ2roi4FotpMCNBcoNCUpOTsmw7soGYAE3gXkGLjd3Tc2n9YcU+c4uXZcoaCJsQ0l7TEkZTu8OMowhqEMbBYA1tR+YXAIytDRGuad0zbclxODa3vgwCA0kNpiSd0ls2LBrYTvJsZFEUvj2BiHMHBu7UEgyBI3jXSFgbX/ALy2p8SgXljhLgB3QdD3Tx4ACI5raxNF1Om9+ZpaBmmH5iRmJtYOA1tB7x4oV2bt1zQ5xDXktMB0wHEROuoS0m+x1/ZeFTk23SQUbANarSNZ4yASQWBxccti7L+AD8xOsop2J2g/BVdI3PNiJ0D76f1eaCNgbdqGk5gfAdd4zQDHHjvtvXbU2yYdUc6XRcnUgCI/RZe9alVHopez8csbt/qeuhyyto7ch7aFGHVXGCdQziTxIF4XmOD7WMc0Mb8Yk/gbmMngADdEnYcvdXqOdTNMNYA0O175uf8Aj6q2pz+6xuRyMemt31R6JQbukmN539U3FV2tCRrrKhjCSvOzytR+rGoY05HHFSlYQ5U8HhqpBLogcE9hhZK1W5DbhHpFkeJbFtyCquJqYPEFtM9x3eDDoRvHUaSLxCMsTVlBXb2l3KT97Xls/wCppP8A0C39m5tmfaujKanGpY7fYNtk7VbWbmaYI+Zu9p/Uc1qNqLyPYW1nNOdphzfm/qH2XpWz8YKjA5p13HUHeF6dO0cScNrNT4i7Mq4PNOzHkiUJsy7Moc5SfFKhCeV2ZQiol+KoQlfMW1Nh1JhEjWQA0aAAeSH9nQ6swbmy8+GnqQiAlMYF1ZlmfRCLiopj76E++qdm5+aZFhyxdpsFTEMplocA2SCJHeJkEG2gatgnks6vgXfENWk8BzgMzXiQYEW0IsAoiEGM7M4ep+DKdxYYjoDIHgFl4rstVF6dYO4CqJj/AH3d5Qto4+qz/MoEjjTIPoY+qlo7Xoutnyng8Fh/5aoNJ9URPwCL6OKo60nxxYfiDrlJJ83BZ+J2vVz52PbAGU039y/GXQ0H/cV6XE6KtisDTqfOxruoEjodQqPDFl/eSBWhtBpAL3ZCfzWBPJ2jvBX2QQn4jsnRMmmX0j/Q4weo1Pms13ZKsLNrMjmzKfENELKWnfY0WZdwqzob7W40zTpgXkPOm6bXPI+wt970K9qnj4jYgFrQZgH5i7KLjTuuKysmVfAzJaLSeMCJiBEOJ0iSHCNbHdKr1g1tyXBzpGcm1y2ZB7oBJb1k31ljjlDpkZMzzDiBlDTaTcC8G02Ft5zjiw1xiGmTla2YAzmQBA70iTNrcbNPcRo29nviizKNWNdqZcQTLy4iTHdufJQ4mpLjEm5bDSDAa7e4iYJNwAYhvKYcHiYp0+9BJDycrmk5iXtaYu0TAgwbdVkY7aDXAtDmARlZBvAytkkkgTeIOgGqqyKJbFQcZF8wzSI1MwLxa558F52NmPc8sZcyRAnUEiBxR/s3Burv+Fe7XS5ugEflO/M4D2ULim/BvIf3Xs9eDmneCl922TSOv7OiueTBdSq0n5Tma6YIIII8EQbMwbXNca51HdbEyZ33tvW52VqU69dtXFGcwNzrfS60tsYNhoHvNLXEhv5hGjo3KmTJ3SPQ6bDxV9e3YGeyFalRxLnRIBiJg5TqA7cjjs1j2nEVWgRmGYCZgNOk7/mXlmyNmVnVGj5Q43dugcpRzV2YzCVKdVlRznNgmHBwII7zTAjT9Etrob4OKf8Asvpo+8x0o11/NnplOqI6KJxErLwmObUY1zTIcJCfUrRN15ieWXRrlGSw0y6+rE8FRrYgKnXxqpVsRKtunLqaKKiW6lWShftzWGSmzeXl3g0Ef9lsuxLWgkkCNShHaDKuKqlzGktFmCQJA3343Pkul7O07eRSfYw1WRKFeTEoVcjsw3fTeEddmNpZXZQe68Zh1/j6IDxdB7CA5pE3HPmDofBa+w8QYYd7XR4TK9CuGct/EqPXaFaQpg5ZGysTLdVsNcDxWgqNLj7CaSVIRyKQ9FCEUldmKf5JlZ8NJ5KERpdnWz8SpzDB4Xd6wtlzlR2NRyUWDeRmPV1/pCtu3D3b2E7ijUUK5ZXNkNehmAGoHCNehBEjjrdPpWABtAvuvu3md/FM+GxxmBPFhh3jBBUjmNd8wm9p5W+/mtDIax14gtOtxA8x3U+T16/t9kz4RFw4gDdrIG4+M3TXsd+ISBcEOIMxyjn5okH1qhDSWgkgWANieHJU/wC8U392rTgkhsFpuTYAOAgqy2oIgk2Ny4QY11gW0Epxff5SRxEOF+N8wUAVKdJvw4pOLW7ssW32kEDokY6sIyvp1I1DgWk+LZHon1Nm080gFp07rst9d0XtxTK2FqAkscLzZ06nSDIIG6L6dZPBORrdsAWq03MPEd9vgQJ8wFZZtKiRIqs/8h9DolwmaCHtDbxBMzz0Cc/A0zc0wgEgzhCHayrFaBwZrp18IcY3wNNQXFo5eqDe25+HUFQjM007iJ/y3F1p1Nx5rnttDcob4tIHMXVlpDsrnAl0Q7KGGSXBogVDFNgzGPAEzfwOFpsBeQHOJMF4JaA2WsNiQSNcxJPOVljEtexz2PLgP8RoBN3MghrpPdgPdx+bRR4baDnURTB7zTli0gZTDQQZAAgXuYM71XeJPGy/tLENc0w+LtyhojvyHT3jcR11GkrMxWzQGw174uY7uXugSWi8MnQTbLJVZ1R4IqOJmQTJMzcd2dLEacORUuDpuryQRAMzEmbSJiSLcVlLKq6muLTzk6iFmwm0KVFr6bXNLvmlxLuN53ezJTtr7LZiqeWpTBJmHQDBPDhdA1OpWwkfFeXsn5wLD/UN3VHfZ3amdhg6XO+eiTkpbtyZvk088fqeavpvw5yPsWd0E2BA0vxUWL2vbLq52kH7L1/E7Pw2IDs9ME+pHPjZBHa7YVGhUZ8OmBEuk6jgOmqvGcZcs6ek9ozbWP8AQxcHsus1jX5w2LxBOadbqpjduRmbU1HCVv1tsuexjM0hrS0A3DQReBuQdimtfiGtmxMOPJGFSk3I7eTLLFj2Y+t/n9jQ7Pdp3UJM5mEyWH6tO4/X1R5gO0FCu2WVAeRMOHUFYO2dg4dtEfDpwCALkEucdSABMeeij7N9g61SmRUYKTSe6XiXcjk3eJBSeo0+nzXN/C/PkSzZZYOZyXP5CDE4lovIWPjNtU26vE8Bc+QWj/8AFDHWNeprwbHqCoa/9mRpjuVc3Vo/RY49Pp4/NN/tRz5+0U+g/ZGENbvVhDdWs1nQgu49NES06LAIDR5BZ2wcKaNJlOqO+0QTeCRvC36ApnevRYYQjFbKo5WXNKcrkzG2rs1lWm5rmAg68W/1NPEfRedYXDmkXsJ0dY8QQ0g+IIK9pYxkFeV7YZOIa0bw0eb3R/xyq+WPFmumm7aCrZNaLIjw9cocwMSERUXLCjVsnz8k2TwStfG+ysZZFro1YGysZUVWmXuYz87gPCbq1yUuyKOauTuY0+Zt9D6KJW0gp1b8G+Ao3b+I0Exz18lNCrVnARLsskxeJ4g6g68F0Tnse0nWCCPzAT0BCk0FxoOqZTba5m+oH1i2vRWGieBQYUQimLAenK8x1+qVwNhrf6X+ysYmAwmBMb1m4bFZ5g6WPXfr4Irkj4LBgkyLaXE9f0TP7u06GP8ATx9wlp1Dv33Skgi9ifDXn70UARvok65XN1hw9fKPJIy02cBG8y2/CTaOCsPFrHW3vwXEmdOdvL7+SNkoh+LN2wRwBg+RsfRSBvhy0+hhc5rSY5X3TPspooDjYWGvvioQquHJYPa/C56OaJLCD/tNnfofBEFkyphxUBYdHS09DYrntWPQltaZ41/kmmyjTAfmbLgJLoIEcHE5tDMT0WjtHNi6sUWspMHykzmN5JmdSbwijtZ2feysDTkNjuxcttBExbqpuy+xgxwzjSIn0SMozUkkufJ04RTjvdVQO4PsHWI7znOabmHkA+Gi0aWwDR7rGAcb/Veh1q2UQ1YWMx7m3MTyVsyinRbT5G/likBW08AIIcOR4Hkg1lB2FflJPwXGGO/IfyO5cD4I/wBq7SDyZ366LJdSY5rmOEgi/ilt9Wuw9m06y4+eGamwqpawEzlOhIPjqr+22U3sDnQSGkGY+XeSdwHFebYDtB8Jxw9R92OIGbQjUGdBIIPiU7afaltVjsOx3cJ/xHC2YCIYD+WxJO+VFhlv+h55aZyyUjDoYhlSo/vOFOTlix5G/HWOag2jlblLPnB8+XNVaeFz1i2mYCMezPZRzcbRFYfKPixmBBgd2YtqQfBOycYK+yOnOcljcn2+vgPOw2wXtYypXGasRZuvwwRp/q4nw6mGKaKfzEE7xwTMC0tbnaYiG+ay9rPcWmFy29sba+J8/RI8/mzTyycpvktt2myYkSrNIh68sxONqMqAmYR5siq8sGbksNzu5GaZp7QwgiHDchDG1RRdDnADcTvRVjMV3YO7RAXbuh8bCvP4m99pHFl7dRI8U/psqjOo9Cy68jdsdqadJhGaCRA/MZ3NH66BZ79ngVWVxUD21Jc24zAxAaeMDeLWQHs/ByTUN447z+w+qJcNh6lBjah71Mjvt0ygmR0GnQhdnHtyPbJ/7Ncu/BDfBX59AwwzzIW9QlC2ya+cTflO8cfuiWg+270WU4OEnFm+LJHJFSj0ZeptG8+SsteBoqTSeCna7lHmqplmiyXiFe7OU/8ADc/87vQafU+SxcVUhp52RPgaWSm1vBonqbn1JWuFXO/BTK6h6k7/AK2VRzGvJhwJiIkgxfUDroQrLtegn36plnagGL95pBHA3H0TgmznVADeA6IEnf1HUKWgSTcRG+QR4HXzSNgjvDncTqnYWkCQ6+/fZBhRHtepDevv7IZwdXvG2vgb9YK0e0zhoBc2taZ13ibSsjB1csZ2loFry7kDO+0rbGqiUm+QkY6bT4H3KsAmdOf6fdZ+EcTHylp3g/8AQiN24q18QNN3ATIEmNOtlSSCmTwJ4en871wBufry/eVFisY2m3M8gCwGawJOgnRLhqktB0nSNI48L6qobV0PzWJI/XRI0Dj6rgdBYj7KSeShCjI4KTBxnFuP0UMp9GoA4FIWPUabqQMk6lZuOqAGw4SpsRjAJusTFYnW6pkzJKhvT4ZN2x76h3aFYm1XGDvKtYnHd2AsHF4ybbyufknb4OpijtdmC5xJvqE6mXBOr4cgym4mqGsniqTfg6PFWB+0cAx+LM7w0nwEfou7S7Oosj4RJ7rbkQZjvC26ZTcPha+JxD3UmGAYDzZtrG++86SiAdiq1QDPVHQNn1J/RMuahSk/BzljjkjJxXkFNnv+GWHcZHr/AD5I17J7RacWwfmY5o62P0BVep2AdH+Z5s//AEsyr2bxWEqMrMPxPhuDoFnEA3EHWRI13quSeLKmlLli0oZI4HjlH+57fhqvcy7pB8R/KdTiRIBGhHI2WTszabalNrmmzgCPLerrcUAFzVkaryjz08bTplDaPZOlVjM3M2QbEiCLjRbjcM1lMNFoOngqTcfl3pMTtVoGqO9O0iigUdtVYFih7Hvmk7NpDvoVNtHH53LG2zXmk5rdXDKPHU+UpjTQ5DGNyQNbCw4cWtPyt15n90a0mWvBHRC2z6JYQEV4WpIXUTo6MuTBqNdhqgDfkJzM4f1MPh6RvajDZ+ID2hzdD7IPNZe0sH8RhYeoPAjQrO7M7RNN5pP3mCOD9PI2Hknv5+P/ALR+6OX/AMXNX9E/s/8AIcUz08gpWAcvBVKFbkrTanL6JM6IjWZ61Nm6ZPQXPpKKgUOdnWZqtSpwEDqf4PmiElNadfDfkw1D5S8CONt0zvFvPcnNBI4TaxzDzI4Sqz3NzAEkGIFyNd0ixPKFYp7tTG+0+n2TAuTOmOM281IxgIJuD5KPoVLUdDPBUZYFNrE/FAzk6kAgWi2ojiptnMNzG/dyt9/NUKr81Z5n5QAL7zJP1atMVvhU51gWB1J3JquEkYWuWztoYynTbJbLhJA0iN56wet+aHdm9sC95b8SkBplkdI1Q92n2jUr1jhaboOXPVdqGjRrfH6CUEbQwNWiYe2BucLtPj91eU8eH4HG33+noKwhlz/GpbV2Xn6s9U2hUdiMRTpOp/Dpg5nZdHcSItJHdB4lFjsT+UgAWhwLY0EZhpu3LxHY/aerShrnF9PgbxzBK9N7HbUFcOa85oDS0nex0xPT9QqrFCUXkg+nVdwQvFm2zXMu/bjt9AvpjU8PPnpfgnMqg6OB6EeSgZTEBwJHQ2j+FIWON+74iVg0Pohy9F2XkowSlBcubaH6MjbjXsOcXafQrDr450SCPujN0kEESDqFjYrYDDdktncbj9kvkxXyjpYNVFR2z/cFcRXcZlZD6/eRZiez9QyA5gHV32Q9tPZD6F3EOB3t0ngZ0WCxtdhmOeDfUjq4wBplUMM3++VMjZ+Ew946ZncByQ5tjF1y/wCHkyjdec3MHReo9j9kNpsDTaBLjz3qrxNV5Y1DOpJ+F9zT2RsNoAa0AAeQW9T2Kxo1lZFXazWyG/LxS4Xbwkg8kN+GHFWK5lnlynX0LeKwMaQszH4SQQQtv4sjML8Puqe0quYaJecIO2imPJJOmec4vHOwdQkAmnPeaPwz+JvHmFp0drtqtDmOkHgVJt/CiowiLi4Xm2Gw9SnUcWOLAD7sr4MKzeonrMKT3B/WxLhvUBxh4lBFftLiA/JDH8LH7q5S2lXfDQGZjwabeZKZ/hGnyIe6sIquJO7U+iuYTBWl1yq+ydlEAOe4udz3dBuWwGQt4wUC8caXJm4vDRuUuy68WhXKrZCziMrpV7NAga6RohntNg8pFZojRr/+rv08kSbNxIIg/orGMwjKjHNOjgQfFbYcjxzUkL6nCsuNwZT2HtH4tMGO8LO6jf46rXq1oaTG5A+wXmhiDSeYk5D1Hyu8f+yLa0y1ovN1pqseyfw9HyjLQZfewSl1XD/QJOz1DLRB3uJd+g+nqtGp+9uXD0SUaYa1rRo0AeQhdvPEWgH3x9EzCO2KRSct0myu0MLrOGbmMr7bpEGPAqc1xMWDjpIJGsRPh6hOa4xPDc4QZ9ncpZgaaDqrFBrJOsAzFjm+oBCdtWpDCocrQ4ECTaY3xp6wszb+02PHw2nvuBABBG7j0lRRuSI5UjH2e0GXkfO7MZ3gfLY2/KqfabbLaNJ9UzDBAF7vOgA8R5jgrGMxbmhrMvedDBluAT6RceSCu2WIh9GQX0qTiXRvdFjGkSSYNrAJ/GqjLJXQQzzTlHFfzP7DuzuDcxhqVP8ANqnPUPM6N6NFvNa1QNcIcAQdQbz4LPwWPp1RLHA8RvHUblYC5rbbtnSSSVIG+0OxadNpqUzluAGagkmwbvB5fRGXYXCOpsL+TaY5kfORxEn0KFmh2KxQa27absreBqHV3Rokr03CYUU2tpsPdaPZkce8U7ij7vE33l+F/n8Cc37zKl2j+X/4vybVOqHDgTbh19JVsdfoqGEBtI/VW8jTvjxj0S8hhFfM7gkzngVNlHFIWjiubtY/aGB55pC/qpMnNdk5qUw2inUeOCz8ZRa4EESDqCFr1GqlVYfYVHEtGQAbZ2Jle0gZqeZp5tIcL9Ofmilzi2QLSFLiWnf79FmY3FEEeSWzfCrR19BLe3FjMVhcwgFDQq16NZpZSc9peBEfNJ3IvwWIzQeCI8HUpkS4XF5+yQUdz7fqbazcROzNbGnBYtfFidfBXNrbQBmLRYdOqF62NklUkrlSKQh8NyLOLq2K8z7VVnNrBrPxDMfOEc18TY80E4ukcRXLh8o7oPEDWPGbp3RxcZWLaprbRU2bgy6zRLjqTuCNtibJDBpJ3lQ7K2flAAt5ohw7CBqPNPXYgx4pkbkuY8FJJVXaG0WUW5nmOHEngBvUUW3SA5KKtkxqclk7UxlJnzOAPDf5C6wNo9pK1U5acsHBvzHq78Ph5qrhNi1H3MieGvmmFgjHnI/07iT1UpusUb+vY3ae3RTE93kHOufBocR4wmP7cPmGtYPBx/UfRQM2A1ty2et1aobOBsGo+9wx6Rv1A8Gon8069EZ9baRr1M5EOgbiNND1+y0KHaiq2qCRmc2CO7NrEWCdW2M8XDfJXuxWFZUrVGuMOLAW9A4Zh6hP4skc2O9q+H8HMy4J4M+1TdSXX6mjQ/tFcDD2M8nMPqStvBdtKLwAQWbzbO30IPop8V2czNgOBniPe5C2M7KQ7KGQb3YcumvdP10Wm7G+sf2J7jUR+TJfqj0XBYynVALHtdxymY5X0vG4K27d5+X7wvKBgcThzmY4vjc6WuHJrgb+BE80QbE7Yg9ysSCLEuHeb/qFp6iP9MAlVlgvmDstDVuL25lX17BRjXuhxZAcNMwkHeRIPTfuWBh8LVqP+K8MblkWJM84IEeaJcI7M0GxBvIMgzdMxeGBbAsT4a69d6zTpjTV8gNtPEvY7ORGobyLgfoCfJDeKxDGiXkAExfQzuXoON2X8WmabjBJsSNHDf5SvNO0eyHtIY/uubJafwuBj7BdjS5UsTUFcvHk4mt07lnUpuo+fBQxWw7iph35TqL2/wBrh9LqVm0cXkNJ1LvkQKlgBxLotbl5JmxaTqZdmd3eAuJ48kQbGwDsTUgD/Cae+7Sf6BzPoqS0eGcPeyTh5ReGr1EJ+5i1NdmaXYnZbaLGvJAnutzAyQT3nWNi48tI4ovpsmSLwep4ddZSMgNjLA3CLQLCOFgFbw2Bb8wmeMm55pDJk3O/2Opix7I1+/qWsJpIMj3qDcKy0+7/AGTBTcN88AfumuaTch4PJ1vBLvk36EJHL6rgR7KbnStcuePC29lcAk9711kCDKhHFVqg5hWKgVd49ygwop4hh9kLE2lhswtqFv1KY9ws/EUOCzlG1TN8WV45KUeoLU8U6md/vktLD7cspsThGu+Yel1QOyRudHgPukpaXng6f8dGa+JckGO2jmWX8UrXdsj+ufD90ypsQn5XQjDTUYT1KfQw8Q4u7gOup4chzVjAYABatLYjmq7R2ad4+ibjFRVISlNydsgw1CN6vNJHD0UwwcbvfgVBigGNLnWAEk3VirKG2dqigzMQCTZrfzH7c0G0MPWxlQu83fhaPytG5L38ZiN4B0/oYN/U/Uo72fhW02hjLAe5TjfuI1/U/sc9J6md/wBC+5R2ZsGlSHyyd5JWuwcAUpB4pGk8km5Nu2PqKSpCVWAjQ+SgoHKf2V1s+yq9emdYUIX8TXBYANSfohmi04bHsJsC5p/2Vh+mb0WtSJfUZTve3/kYn6qL+0mhD6VQWzMLf/rMj/2XR9nczl6Ucv2v8OKLXVO/7B5SfIIgEgaHfP8ABT6ZBsWubF7wR53A9Cs/ZuIz02vgd9rX307wG+LLRpSWjdO6cw53PJbSVEi7VkVfD5hBaRvzDLv4zMaob2x2ap1QYIFQaOAgjkb3tP6QjKTGkzayjxQYQS6AADM21577SpCbi+CuTHGUakebbH2zWwVQ0aw7vCTF/wATDwPD6FW8d22rVHZMO0m0w1udwn8xIgeXiVnYui7HYo02dxo1cRJYwGwv+Ind14IjNGjgqQYG2M2ae+4i0jedNSd4unMjjF21ycnTY8mWNRk1C+PIPnaG0h3slTwNI/8AEE/RP/8A6H4oLK7QXb2vbld4bncdG8pKkO1qhcQOZaC2TG4TF/3UmKwzq7cr2ZvyvEEgneDqOlwVSOaLfQalo5wVwk/1djMFsOhVMmmQJNw52W1o1seR0RdhdmspUw2nYDSIif5Xn+ydrVcJVy1DnY6CR+YCwc2dHCIjlB3FemYNzHta9sOa4Agi0+aGdz7ttFtLOErW1KXdDaWE03/t7CvBgt705+SVrb69J9f0Ug6JRyseSEi6a4En905sdN65oMIBM33ollcuXPY8h0hISOK5cpZKI3DoonrlygUQPbzVerTXLlUJRrUjuKqupuC5cqlx1IcVaYwb0q5CwsmY0KSBy9Uq5GytDSw8vT7LA7UvApua6wykm4vbujxMBcuTGmipZUmKa2Tjgk14MXsfhIpmoRd5/wCIsP1KK6FL2Vy5VzSbm2/Jpp4qOKKXglFEewV393C5csTccKHv+UtXD21XLkQEXZ7CzXc/c0GP/X7nwUP9oTP8CmeDyPNpP/VcuXU9nrhepy/a/wAsl4SLfZuuThaJOcRTADmwdLXF+G8IkokHrHQmfZ81y5bZOrM8PyL0RaYDOvn6fqsztJWLcPVMfhd9ly5Ux/Og6j+VL0YJdjXhtKvVnvuqka3OVoyg+Lj5rXo4LvFzoe/STuiwA4DwXLlvl+ZmGj/lR9DYpbMZADmek6fvCa/YdL8ADZn5fdjJC5cltzHKMza2w3OaQQKo3h9jzIIu12neEb1n7L2hUwj20q0/CuGkbhwMzpviOOlhy5GUntAoLcGFCu0tlpDt8AjQ3HLSFM3SYI5aEeUhKuVZKgxdnE+/4Sg9fP8AdcuQLH//2Q==",
        link = "https://www.youtube.com/watch?v=4o2-M1C6T5I&t=76s&pp=ygUTZGlhYmV0ZXMgbWFuYWdlbWVudA%3D%3D"
    ),
    MiniCourse(
        title = "Diabetes Management for Beginners",
        imageUrl = "https://i.ytimg.com/vi/CMy5jvSXB6M/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=CMy5jvSXB6M"
    ),
    MiniCourse(
        title = "Understanding Nutritional Labels",
        imageUrl = "https://i.ytimg.com/vi/f5IVZHH5P8U/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=f5IVZHH5P8U"
    ),
    MiniCourse(
        title = "Meal Prep for Weight Loss",
        imageUrl = "https://i.ytimg.com/vi/LVzyfpHxJio/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=LVzyfpHxJio"
    ),
    MiniCourse(
        title = "How to Eat Healthy on a Budget",
        imageUrl = "https://i.ytimg.com/vi/TvDAoZW5RzE/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=TvDAoZW5RzE"
    ),
    MiniCourse(
        title = "Beginner’s Guide to Intermittent Fasting",
        imageUrl = "https://i.ytimg.com/vi/kaxKj2d3FGQ/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=kaxKj2d3FGQ"
    ),
    MiniCourse(
        title = "Managing Sugar Cravings",
        imageUrl = "https://i.ytimg.com/vi/SprJ9dO1nL8/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=SprJ9dO1nL8"
    ),
    MiniCourse(
        title = "Understanding Carbohydrates",
        imageUrl = "https://i.ytimg.com/vi/BC9xI6hRP4w/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=BC9xI6hRP4w"
    ),
    MiniCourse(
        title = "Healthy Breakfast Ideas",
        imageUrl = "https://i.ytimg.com/vi/49HqycHbZWQ/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=49HqycHbZWQ"
    ),
    MiniCourse(
        title = "Nutrition for Diabetes Control",
        imageUrl = "https://i.ytimg.com/vi/XeHLPS7zszs/hqdefault.jpg",
        link = "https://www.youtube.com/watch?v=XeHLPS7zszs"
    )

)